package fintech.workflow.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.calendar.spi.BusinessCalendarService;
import fintech.workflow.*;
import fintech.workflow.db.*;
import fintech.workflow.event.ActivityCompletedEvent;
import fintech.workflow.event.ActivityStartedEvent;
import fintech.workflow.event.WorkflowCompletedEvent;
import fintech.workflow.event.WorkflowExpiredEvent;
import fintech.workflow.event.WorkflowStartedEvent;
import fintech.workflow.event.WorkflowTerminatedEvent;
import fintech.workflow.spi.ActivityListener;
import fintech.workflow.spi.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static fintech.workflow.db.Entities.trigger;
import static fintech.workflow.db.Entities.workflow;

@Slf4j
@Component
public class WorkflowServiceBean implements WorkflowService {

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private WorkflowRegistry workflowRegistry;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ActivityCallables activityCallables;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private BusinessCalendarService calendar;

    @Autowired
    private DynamicActivityListenersService dynamicActivityListenersService;

    @Transactional
    @Override
    public Long startWorkflow(StartWorkflowCommand command) {
        log.info("Starting workflow: [{}]", command);

        WorkflowDefinition definition = workflowRegistry.getDefinition(command.getWorkflowName());
        WorkflowEntity workflow = new WorkflowEntity();
        workflow.setClientId(command.getClientId());
        workflow.setLoanId(command.getLoanId());
        workflow.setApplicationId(command.getApplicationId());
        workflow.setStatus(WorkflowStatus.ACTIVE);
        workflow.setVersion(definition.getWorkflowVersion());
        workflow.setName(definition.getWorkflowName());
        workflow.setParentWorkflowId(command.getParentWorkflowId());
        workflow.setAttributes(new HashMap<>(command.getAttributes()));
        List<ActivityEntity> activitiesToStart = new ArrayList<>();
        for (ActivityDefinition activityDefinition : definition.getActivities()) {
            ActivityEntity activity = new ActivityEntity();
            activity.setStatus(ActivityStatus.WAITING);
            activity.setName(activityDefinition.getActivityName());
            activity.setActor(activityDefinition.getActor());
            activity.setNextAttemptAt(TimeMachine.now().plusSeconds(activityDefinition.getInitialDelayInSeconds()));
            activity.setWorkflow(workflow);
            activity.setUiState(activityDefinition.getUiState());
            workflow.getActivities().add(activity);
            if (activityDefinition.getInitialStatus() == ActivityStatus.ACTIVE) {
                activitiesToStart.add(activity);
            }
        }
        workflow = workflowRepository.saveAndFlush(workflow);
        activitiesToStart.forEach(((a) -> startActivity(a.getId())));

        eventPublisher.publishEvent(new WorkflowStartedEvent(workflow.toValueObject()));
        return workflow.getId();
    }

    @Transactional
    @Override
    public void startActivity(Long activityId) {
        ActivityEntity activity = activityRepository.getRequired(activityId);
        WorkflowEntity workflow = activity.getWorkflow();

        log.info("Starting activity [{}], workflow [{}]", activity, workflow);

        Validate.isTrue(workflow.isActive(), "Can not start activity [%s], workflow is not active", activity, workflow);

        ActivityDefinition definition = workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName()).orElseThrow(() -> new IllegalStateException("Activity definition not found: " + activity));
        Validate.isTrue(activity.isWaiting() || ((activity.isCancelled() || (activity.isCompleted()) && definition.isReactivationSupported())), "Can not start activity %s, activity is not waiting or is reactivation not supported", activity);

        activity.setStatus(ActivityStatus.ACTIVE);
        activity.setNextAttemptAt(TimeMachine.now().plusSeconds(definition.getInitialDelayInSeconds()));

        if (definition.getExpiresInSeconds() >= 0) {
            LocalDateTime expiresAt;
            if (definition.isExpiresDuringNonBusinessDays()) {
                expiresAt = TimeMachine.now().plusSeconds(definition.getExpiresInSeconds());
            } else {
                expiresAt = calendar.resolveBusinessTime(definition.getExpiresInSeconds(), ChronoUnit.SECONDS);
            }
            activity.setExpiresAt(expiresAt);
        }

        Optional<String> shouldAutoComplete = shouldAutoComplete(activity);
        if (shouldAutoComplete.isPresent()) {
            String autoCompleteResolution = shouldAutoComplete.get();
            completeActivity(activityId, autoCompleteResolution, "AutoCompleted");
        } else {
            definition.getOnStartedListeners().forEach((beanMetadata) -> runListener(activity, beanMetadata));
            runOnStaredDynamicListeners(workflow.toValueObject(), activity.toValueObject());
            activityRepository.saveAndFlush(activity);
            eventPublisher.publishEvent(new ActivityStartedEvent(workflow.toValueObject(), activity.toValueObject()));
        }
    }

    private void runOnStaredDynamicListeners(Workflow workflow, Activity activity) {
        dynamicActivityListenersService.runOnStartedListenerIfPresent(workflow, activity);
    }

    private void runOnCompletedDynamicListeners(Workflow workflow, Activity activity, String resolution) {
        dynamicActivityListenersService.runOnCompletedListenerIfPresent(workflow, activity, resolution);
    }

    private Optional<String> shouldAutoComplete(ActivityEntity activity) {
        WorkflowEntity workflow = activity.getWorkflow();
        ActivityDefinition definition = workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName()).orElseThrow(() -> new IllegalStateException("Activity definition not found: " + activity));
        return isAutoCompleteByPrecondition(definition, activity)
            .map(Optional::of)
            .orElseGet(() -> isAutoCompleteByPredicate(definition, activity));
    }

    private Optional<String> isAutoCompleteByPrecondition(ActivityDefinition definition, ActivityEntity activity) {
        return definition
            .getAutoCompletePreconditions()
            .stream()
            .filter(precondition -> {
                BeanMetadata<AutoCompletePrecondition> beanMetadata = precondition.getRight();
                AutoCompletePrecondition preconditionBean = applicationContext.getBean(beanMetadata.getBeanClass(), beanMetadata.getArgs());
                return preconditionBean.isTrueFor(contextFor(activity));
            })
            .findAny()
            .map(Pair::getLeft);
    }

    private Optional<String> isAutoCompleteByPredicate(ActivityDefinition definition, ActivityEntity activity) {
        return definition.getAutoCompletePredicatePreconditions().stream()
            .filter(precondition -> {
                java.util.function.Predicate<ActivityContext> predicate = precondition.getRight();
                return predicate.test(contextFor(activity));
            }).findAny().map(Pair::getLeft);
    }

    private ActivityContext contextFor(ActivityEntity activity) {
        WorkflowEntity workflow = activity.getWorkflow();
        return new ActivityContextImpl(workflowRegistry.getDefinition(workflow.getName(), workflow.getVersion()), workflow.toValueObject(), activity.toValueObject(), this);
    }

    private void runListener(ActivityEntity activityEntity, BeanMetadata<ActivityListener> beanMetadata) {
        ActivityListener listener = applicationContext.getBean(beanMetadata.getBeanClass(), beanMetadata.getArgs());
        listener.handle(contextFor(activityEntity));
    }

    @Transactional
    @Override
    public void failActivity(Long activityId, String error) {
        ActivityEntity activity = activityRepository.getRequired(activityId);
        Validate.isTrue(activity.getActor() == Actor.SYSTEM || activity.getActor() == Actor.CLIENT, "Only SYSTEM and CLIENT activity types supported");

        WorkflowEntity workflow = activity.getWorkflow();
        ActivityDefinition activityDefinition = workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName()).orElseThrow(() -> new IllegalStateException("Activity definition not found: " + activity));

        activity.setAttempts(activity.getAttempts() + 1);
        activity.setError(error);

        /*
         * Fixme: OMG! failActivity -> then let's try to guess why it was failed?
         * m.b. because somebody wants to reschedule it?
         * and ... then let's do reschedule?
         *
         * Should be redesigned
         * - Attempts counting is not working for any non system activities, but used there
         * - Attempts counting implemented by hardcode in some activities and can have different resolutions
         */
        if (activity.getAttempts() >= activityDefinition.getMaxAttempts()) {
            log.info("Activity [{}] of workflow [{}] max attempts [{}] limit reached, failing activity", activity, workflow, activityDefinition.getMaxAttempts());

            activity.setStatus(ActivityStatus.FAILED);
            activityDefinition.getOnFailedListeners().forEach(listener -> runListener(activity, listener));
            activityDefinition.getOnClosedListeners().forEach(listener -> runListener(activity, listener));

            if (activityDefinition.isTerminateWorkflowOnFailure()) {
                terminateWorkflow(workflow.getId(), "ActivityFailed: " + activity.getName());
            } else {
                cancelTriggers(activity);
            }
        } else if (activity.getActor() == Actor.SYSTEM) {
            LocalDateTime nextAttemptAt = TimeMachine.now().plusSeconds(activity.getAttempts() * activityDefinition.getAttemptTimeoutInSeconds() + 1);
            log.info("Update activity [{}] of workflow [{}] to retry at [{}]", activity, workflow, nextAttemptAt);
            activity.setNextAttemptAt(nextAttemptAt);
        }
    }

    @Transactional
    @Override
    public void completeActivity(Long activityId, String resolution, String resolutionDetail) {
        ActivityEntity activity = activityRepository.getRequired(activityId);
        WorkflowEntity workflow = activity.getWorkflow();

        log.info("Completing activity [{}] with resolution [{}] and detail [{}] of workflow [{}]", activity, resolution, resolutionDetail, workflow);

        activity.setAttempts(activity.getAttempts() + 1);
        if (activity.isCompleted()) {
            log.info("Activity [{}] of workflow [{}] already completed, skipping", workflow, activity);
            return;
        }
        Validate.isTrue(activity.isActive(), "Can not complete activity [%s], activity is not active", activity);
        Validate.isTrue(workflow.isActive(), "Can not complete activity [%s], workflow is not active", activity, workflow);

        ActivityDefinition activityDefinition = workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName()).orElseThrow(() -> new IllegalStateException("Activity definition not found: " + activity));
        Validate.isTrue(activityDefinition.getResolutions().contains(resolution), "Unknown resolution: [%s] for activity [%s]", resolution, activity.getName());

        activity.setCompletedAt(TimeMachine.now());
        activity.setResolution(resolution);
        activity.setResolutionDetail(resolutionDetail);
        activity.setStatus(ActivityStatus.COMPLETED);
        activityDefinition.getOnCompletedListeners().stream()
            .filter((pair) -> pair.getLeft().equals(resolution))
            .forEach((pair) -> runListener(activity, pair.getRight()));

        runOnCompletedDynamicListeners(workflow.toValueObject(), activity.toValueObject(), resolution);

        activityDefinition.getOnClosedListeners().forEach((listener) -> runListener(activity, listener));

        checkWaitingActivities(workflow, activity);
        cancelTriggers(activity);

        if (activityDefinition.getTerminateWorkflowOnResolutions().contains(resolution)) {
            log.info("Terminated workflow due to activity [{}] resolution [{}]", activity, resolution);
            terminateWorkflow(workflow.getId(), activity.getName() + ": " + resolution);
        } else if (activityDefinition.getExpireWorkflowOnResolutions().contains(resolution)) {
            log.info("Expired workflow due to activity [{}] resolution [{}]", activity, resolution);
            expireWorkflow(workflow.getId(), activity.getName() + ": " + resolution);
        } else if (activityDefinition.getCompleteWorkflowOnResolutions().contains(resolution)) {
            log.info("Completed workflow due to activity [{}] resolution [{}]", activity, resolution);
            completeWorkflow(workflow.getId());
        } else if (workflow.getActive().isEmpty() && workflow.getWaiting().isEmpty()) {
            throw new IllegalStateException(String.format("No more active or waiting activities but workflow is not completed: [%s]", workflow));
        }

        eventPublisher.publishEvent(new ActivityCompletedEvent(workflow.toValueObject(), activity.toValueObject()));
    }

    private void checkWaitingActivities(WorkflowEntity workflow, ActivityEntity completedActivity) {
        List<ActivityEntity> alreadyCompleted = workflow.getCompleted();

        List<ActivityEntity> activities = workflow.getActivities()
            .stream()
            .filter(activity -> !activity.getName().equals(completedActivity.getName()))
            .filter(activity -> activity.getStatus() != ActivityStatus.ACTIVE)
            .collect(Collectors.toList());

        for (ActivityEntity activity : activities) {
            Optional<ActivityDefinition> activityDefinitionMaybe = workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName());
            if (!activityDefinitionMaybe.isPresent()) {
                log.warn("Activity definition not found, ignoring: [{}]", activity);
                return;
            }
            ActivityDefinition definition = activityDefinitionMaybe.get();

            boolean waitForAllSatisfied = !definition.getWaitForAll().isEmpty() && definition.getWaitForAll().stream().allMatch(activityToWait ->
                alreadyCompleted.stream().anyMatch(completed -> activityToWait.getLeft().equals(completed.getName()) && activityToWait.getRight().contains(completed.getResolution()))
            );
            waitForAllSatisfied &= !definition.getWaitForAll().isEmpty() && definition.getWaitForAll().stream().anyMatch(activityToWait ->
                activityToWait.getLeft().equals(completedActivity.getName()) && activityToWait.getRight().contains(completedActivity.getResolution())
            );


            boolean waitForAnySatisfied = !definition.getWaitForAny().isEmpty() && definition.getWaitForAny().stream().anyMatch(activityToWait ->
                activityToWait.getLeft().equals(completedActivity.getName()) && activityToWait.getRight().contains(completedActivity.getResolution())
            );


            if (waitForAllSatisfied || waitForAnySatisfied) {
                startActivity(activity.getId());
            }
        }
    }

    @Transactional
    @Override
    public Workflow getWorkflow(Long workflowId) {
        WorkflowEntity entity = workflowRepository.getRequired(workflowId);
        return entity.toValueObject();
    }

    @Transactional
    @Override
    public Workflow getRootWorkflow(Long workflowId) {
        Workflow rootWorkflow = getWorkflow(workflowId);
        if (rootWorkflow.getParentWorkflowId() != null) {
            return getRootWorkflow(rootWorkflow.getParentWorkflowId());
        } else {
            return rootWorkflow;
        }
    }

    @Transactional
    @Override
    public Activity getActivity(Long activityId) {
        ActivityEntity entity = activityRepository.getRequired(activityId);
        return entity.toValueObject();
    }

    @Transactional
    @Override
    public List<Workflow> findWorkflows(WorkflowQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getClientId() != null) {
            predicates.add(workflow.clientId.eq(query.getClientId()));
        }
        if (query.getLoanId() != null) {
            predicates.add(workflow.loanId.eq(query.getLoanId()));
        }
        if (query.getApplicationId() != null) {
            predicates.add(workflow.applicationId.eq(query.getApplicationId()));
        }
        if (!query.getWorkflowNames().isEmpty()) {
            predicates.add(workflow.name.in(query.getWorkflowNames()));
        }
        if (query.getStatuses().length > 0) {
            predicates.add(workflow.status.in(query.getStatuses()));
        }
        List<WorkflowEntity> entities = workflowRepository.findAll(ExpressionUtils.allOf(predicates), workflow.createdAt.asc());
        return entities.stream().map(WorkflowEntity::toValueObject).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Optional<Activity> findActivity(Long clientId, String workflow, String activityName, ActivityStatus... statuses) {
        List<Activity> activities = findActivities(clientId, workflow, activityName, statuses);
        Validate.isTrue(activities.size() <= 1);
        return activities.stream().findFirst();
    }

    @Transactional
    @Override
    public List<Activity> findActivities(Long clientId, String workflow, String activityName, ActivityStatus... statuses) {
        BooleanExpression expr = Entities.activity.name.eq(activityName)
            .and(Entities.activity.workflow.clientId.eq(clientId)
                .and(Entities.activity.workflow.name.eq(workflow)));
        if (statuses.length > 0) {
            expr = expr.and(Entities.activity.status.in(statuses));
        }
        List<ActivityEntity> activities = activityRepository.findAll(expr);
        return activities.stream().map(ActivityEntity::toValueObject).collect(Collectors.toList());
    }

    @Override
    public Optional<String> getAttribute(Long workflowId, String key) {
        WorkflowEntity entity = workflowRepository.getRequired(workflowId);
        return Optional.ofNullable(entity.getAttributes().get(key));
    }

    @Transactional
    @Override
    public void setAttribute(Long workflowId, String key, String value) {
        log.info("Updating workflow [{}] attribute [{}] to [{}]", workflowId, key, value);
        WorkflowEntity entity = workflowRepository.getRequired(workflowId);
        entity.getAttributes().put(key, value);
        workflowRepository.saveAndFlush(entity);
    }

    @Transactional
    @Override
    public void removeAttribute(Long workflowId, String key) {
        log.info("Removing workflow [{}] attribute [{}]", workflowId, key);
        WorkflowEntity entity = workflowRepository.getRequired(workflowId);
        entity.getAttributes().remove(key);
        workflowRepository.saveAndFlush(entity);
    }

    @Transactional
    @Override
    public void updateLoanId(Long workflowId, Long loanId) {
        log.info("Updating workflow [{}] loan id to [{}]", workflowId, loanId);
        WorkflowEntity entity = workflowRepository.getRequired(workflowId);
        Validate.isTrue(entity.getLoanId() == null, "Workflow already has loan id: [%s]", workflow);
        entity.setLoanId(loanId);
        workflowRepository.saveAndFlush(entity);
    }

    @Override
    public void resetInitialDelay(Long activityId) {
        ActivityEntity activity = activityRepository.getRequired(activityId);
        WorkflowEntity workflow = activity.getWorkflow();

        log.info("Resetting delay on activity [{}], workflow [{}]", activity, workflow);

        Validate.isTrue(workflow.isActive(), "Can not reset delay on activity [%s], workflow is not active", activity, workflow);

        ActivityDefinition activityDefinition = workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName()).orElseThrow(() -> new IllegalStateException("Activity definition not found: " + activity));

        activity.setNextAttemptAt(TimeMachine.now().plusSeconds(activityDefinition.getInitialDelayInSeconds()));

        activityRepository.saveAndFlush(activity);
    }

    @Transactional
    @Override
    public void suspend(Long workflowId) {
        log.info("Suspending workflow [{}]", workflowId);
        WorkflowEntity workflow = workflowRepository.getRequired(workflowId);
        workflow.setSuspended(true);
    }

    @Transactional
    @Override
    public void resume(Long workflowId) {
        log.info("Resuming workflow [{}]", workflowId);
        WorkflowEntity workflow = workflowRepository.getRequired(workflowId);
        workflow.setSuspended(false);
    }

    private void completeWorkflow(Long workflowId) {
        WorkflowEntity entity = workflowRepository.getRequired(workflowId);
        entity.setStatus(WorkflowStatus.COMPLETED);
        entity.setCompletedAt(TimeMachine.now());
        WorkflowDefinition definition = workflowRegistry.getDefinition(entity.getName(), entity.getVersion());
        // run on complete listener
        definition.getOnCompletedListeners().forEach((listenerMeta) -> {
            Workflow workflow = entity.toValueObject();
            WorkflowListener listener = applicationContext.getBean(listenerMeta.getBeanClass(), listenerMeta.getArgs());
            listener.handle(new WorkflowListenerContextImpl(definition, workflow));
        });
        cancelActivities(entity);
        cancelTriggers(entity);
        eventPublisher.publishEvent(new WorkflowCompletedEvent(entity.toValueObject()));
    }

    @Transactional
    @Override
    public void terminateWorkflow(Long workflowId, String reason) {
        WorkflowEntity entity = workflowRepository.getRequired(workflowId);
        log.info("Terminating workflow [{}]", entity);
        if (entity.getStatus() == WorkflowStatus.TERMINATED) {
            log.info("Workflow already terminated");
            return;
        }
        cancelActivities(entity);
        cancelTriggers(entity);
        entity.setStatus(WorkflowStatus.TERMINATED);
        entity.setCompletedAt(TimeMachine.now());
        entity.setTerminateReason(reason);
        WorkflowDefinition definition = workflowRegistry.getDefinition(entity.getName(), entity.getVersion());
        // run on terminate listener
        definition.getOnTerminatedListeners().forEach((listenerMeta) -> {
            Workflow workflow = entity.toValueObject();
            WorkflowListener listener = applicationContext.getBean(listenerMeta.getBeanClass(), listenerMeta.getArgs());
            listener.handle(new WorkflowListenerContextImpl(definition, workflow));
        });
        workflowRepository.saveAndFlush(entity);
        eventPublisher.publishEvent(new WorkflowTerminatedEvent(entity.toValueObject()));
    }

    @Transactional
    @Override
    public void expireWorkflow(Long workflowId, String reason) {
        WorkflowEntity entity = workflowRepository.getRequired(workflowId);
        log.info("Expiring workflow [{}]", entity);
        if (entity.getStatus() == WorkflowStatus.EXPIRED) {
            log.info("Workflow already expired");
            return;
        }
        cancelActivities(entity);
        cancelTriggers(entity);
        entity.setStatus(WorkflowStatus.EXPIRED);
        entity.setCompletedAt(TimeMachine.now());
        entity.setTerminateReason(reason);
        WorkflowDefinition definition = workflowRegistry.getDefinition(entity.getName(), entity.getVersion());
        // run on expired listener
        definition.getOnExpiredListeners().forEach((listenerMeta) -> {
            Workflow workflow = entity.toValueObject();
            WorkflowListener listener = applicationContext.getBean(listenerMeta.getBeanClass(), listenerMeta.getArgs());
            listener.handle(new WorkflowListenerContextImpl(definition, workflow));
        });
        workflowRepository.saveAndFlush(entity);
        eventPublisher.publishEvent(new WorkflowExpiredEvent(entity.toValueObject()));
    }

    private void cancelActivities(WorkflowEntity workflow) {
        workflow.getActivitiesByStatus(ActivityStatus.ACTIVE, ActivityStatus.WAITING).forEach((activity) -> cancelActivity(workflow, activity));
    }

    private void cancelTriggers(WorkflowEntity workflow) {
        triggerRepository.findAll(trigger.workflowId.eq(workflow.getId()).and(trigger.status.eq(TriggerStatus.WAITING))).forEach(this::cancelTrigger);
    }

    private void cancelTriggers(ActivityEntity activity) {
        triggerRepository.findAll(trigger.activityId.eq(activity.getId()).and(trigger.status.eq(TriggerStatus.WAITING))).forEach(this::cancelTrigger);
    }

    private void cancelActivity(WorkflowEntity workflow, ActivityEntity activity) {
        log.info("Cancelling activity [{}] of workflow [{}]", activity, workflow);
        activity.setStatus(ActivityStatus.CANCELLED);
        workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName()).ifPresent(definition -> {
            definition.getOnClosedListeners().forEach((listener) -> runListener(activity, listener));
        });
    }

    private void cancelTrigger(TriggerEntity trigger) {
        log.info("Cancelling trigger [{}]", trigger);
        trigger.cancel();
    }

    @Override
    @SneakyThrows
    public void runSystemActivity(Long activityId) {
        ActivityEntity activity = activityRepository.getRequired(activityId);
        activityCallables.systemActivityCallable(activity.getId(), activity.getWorkflow().getClientId()).call();
    }

    @Override
    public void runSystemActivity(Long workflowId, String activity) {
        Long activityId = queryFactory.select(Entities.activity.id).from(Entities.activity).where(Entities.activity.workflow.id.eq(workflowId).and(Entities.activity.name.eq(activity))).fetchOne();
        Validate.notNull(activityId, "Activity [%s] not found in workflow [%s]", activity, workflowId);
        runSystemActivity(activityId);
    }

    private void runSystemActivityOnly(Long workflowId, ActivityDefinition activityDefinition) {
        if (activityDefinition.getActor() != Actor.SYSTEM) {
            log.info("Skipping non-system activity {}", activityDefinition.getActivityName());
            return;
        }
        runSystemActivity(workflowId, activityDefinition.getActivityName());
    }

    @Override
    @SneakyThrows
    public void runBeforeActivity(Long workflowId, String activity) {
        Workflow wf = getWorkflow(workflowId);
        WorkflowDefinition definition = workflowRegistry.getDefinition(wf.getName(), wf.getVersion());
        Optional<String> currentActivity = wf.getCurrentActivity().map(Activity::getName);

        Validate.isTrue(currentActivity.isPresent(), "No active activity for workflow [%d]", workflowId);

        List<ActivityDefinition> activitiesToRun = definition.getActivities(currentActivity.get(), activity);

        activitiesToRun.forEach(activityDef -> runSystemActivityOnly(workflowId, activityDef));
    }

    @Transactional
    @Override
    public void trigger(Long workflowId, Object event) {
        Workflow workflow = getWorkflow(workflowId);

        // activate
        forEachActivityOf(workflow, ActivityStatus.WAITING)
            .accept((activity, definition) ->
                definition
                    .getActivateOnTrigger(event.getClass())
                    .ifPresent(trigger -> {
                        if (trigger.apply(event)) {
                            log.info("Starting activity [{}] from event [{}]", activity, event);
                            startActivity(activity.getId());
                        }
                    })
            );

        // complete
        forEachActivityOf(workflow, ActivityStatus.ACTIVE)
            .accept((activity, definition) ->
                definition
                    .getCompleteOnTrigger(event.getClass())
                    .ifPresent(trigger -> {
                        if (trigger.getRight().apply(event)) {
                            log.info("Auto completing activity [{}] from event [{}]", activity.getName(), event);
                            completeActivity(activity.getId(), trigger.getLeft().getResolution(), trigger.getLeft().getResolutionDetail());
                        }
                    })
            );

        // fail
        forEachActivityOf(workflow, ActivityStatus.ACTIVE)
            .accept((activity, definition) ->
                definition
                    .getFailOnTrigger(event.getClass())
                    .ifPresent(trigger -> {
                        if (trigger.getRight().apply(event)) {
                            log.info("Auto failing activity [{}] from event [{}]", activity.getName(), event);
                            failActivity(activity.getId(), trigger.getLeft());
                        }
                    })
            );

        // reset delay
        forEachActivityOf(workflow, ActivityStatus.ACTIVE)
            .accept((activity, definition) ->
                definition
                    .getResetDelayOnTriggers(event.getClass())
                    .stream()
                    .filter(trigger -> trigger.apply(event))
                    .forEach(trigger -> resetInitialDelay(activity.getId()))
            );
    }

    private Consumer<BiConsumer<Activity, ActivityDefinition>> forEachActivityOf(Workflow workflow, ActivityStatus desiredStatus) {
        return consumer ->
            workflow
                .getActivities()
                .stream()
                .filter(activity -> activity.getStatus() == desiredStatus)
                .forEach(activity -> {
                    ActivityDefinition definition = workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName()).orElseThrow(() -> new IllegalStateException("Activity definition not found: " + activity));
                    consumer.accept(activity, definition);
                });
    }
}
