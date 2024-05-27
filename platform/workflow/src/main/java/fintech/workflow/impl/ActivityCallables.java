package fintech.workflow.impl;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Striped;
import fintech.Validate;
import fintech.workflow.Activity;
import fintech.workflow.Actor;
import fintech.workflow.TriggerService;
import fintech.workflow.WorkflowService;
import fintech.workflow.db.ActivityEntity;
import fintech.workflow.db.ActivityRepository;
import fintech.workflow.db.TriggerEntity;
import fintech.workflow.db.TriggerRepository;
import fintech.workflow.db.WorkflowEntity;
import fintech.workflow.spi.ActivityDefinition;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import fintech.workflow.spi.BeanMetadata;
import fintech.workflow.spi.WorkflowDefinition;
import fintech.workflow.spi.WorkflowRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@Component
class ActivityCallables {

    private static final Striped<Lock> rwLockStripes = Striped.lock(100);

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private WorkflowRegistry workflowRegistry;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private TriggerService triggerService;

    @Transactional(propagation = Propagation.NEVER)
    Callable<Integer> systemActivityCallable(final Long id, final Long clientId) {
        return buildCallable(id, clientId, this::executeSystemActivity, new FailActivityOnException());
    }

    public Integer executeSystemActivity(Long activityId) {
        ActivityEntity activity = activityRepository.lock(activityId);

        if (!activity.isActive()) {
            log.warn("Activity is not in state active, ignoring: [{}]", activity);
            return 0;
        }
        if (!activity.getWorkflow().isActive()) {
            log.warn("Workflow is not in state active, ignoring activity: [{}]", activity);
            return 0;
        }
        WorkflowEntity workflow = activity.getWorkflow();
        Validate.isTrue(activity.getActor() == Actor.SYSTEM, "Can not run non-system activity %s", activity);

        WorkflowDefinition workflowDefinition = workflowRegistry.getDefinition(workflow.getName(), workflow.getVersion());
        ActivityDefinition activityDefinition = workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName()).orElseThrow(() -> new IllegalStateException("Activity definition not found: " + activity));
        Validate.isTrue(activityDefinition.getHandler().isPresent(), "Can not run activity [%s], no handler class set", activity);

        BeanMetadata<ActivityHandler> handlerMeta = activityDefinition.getHandler().get();
        ActivityHandler handler = applicationContext.getBean(handlerMeta.getBeanClass(), handlerMeta.getArgs());

        Activity activityDto = activity.toValueObject();
        Stopwatch stopwatch = Stopwatch.createStarted();
        ActivityResult result = handler.handle(new ActivityContextImpl(workflowDefinition, workflow.toValueObject(), activityDto, workflowService));
        log.info("Executed system activity [{}] of workflow [{}] in {} ms", activityDto, workflow, stopwatch.elapsed(TimeUnit.MILLISECONDS));

        if (result.isFail()) {
            workflowService.failActivity(activity.getId(), result.getError());
        } else {
            workflowService.completeActivity(activity.getId(), result.getResolution(), result.getResolutionDetail());
        }
        return 1;
    }

    @Transactional(propagation = Propagation.NEVER)
    Callable<Integer> triggerCallable(Long triggerId, Long activityId, Long clientId) {
        return buildCallable(activityId, clientId, a -> executeTrigger(triggerId, activityId), new FailTriggerOnException(triggerId));
    }

    private Integer executeTrigger(Long triggerId, Long activityId) {
        TriggerEntity trigger = triggerRepository.getRequired(triggerId);
        if (!trigger.isWaiting()) {
            log.warn("Activity is not in state waiting, ignoring: [{}]", trigger);
            return 0;
        }

        ActivityEntity activity = activityRepository.getRequired(activityId);
        if (!activity.isActive()) {
            log.warn("Activity is not in state active, ignoring: [{}]", activity);
            return 0;
        }
        if (!activity.getWorkflow().isActive()) {
            log.warn("Workflow is not in state active, ignoring activity: [{}]", activity);
            return 0;
        }

        triggerService.executeTrigger(triggerId);

        return 1;
    }

    @Transactional(propagation = Propagation.NEVER)
    Callable<Integer> expiredActivityCallable(final Long id, final Long clientId) {
        return buildCallable(id, clientId, this::expireActivity, new FailActivityOnException());
    }

    public Integer expireActivity(Long activityId) {
        ActivityEntity activity = activityRepository.getRequired(activityId);
        if (!activity.isActive()) {
            log.warn("Activity is not in state active, ignoring: [{}]", activity);
            return 0;
        }
        if (!activity.getWorkflow().isActive()) {
            log.warn("Workflow is not in state active, ignoring activity: [{}]", activity);
            return 0;
        }
        WorkflowEntity workflow = activity.getWorkflow();

        ActivityDefinition activityDefinition = workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName()).orElseThrow(() -> new IllegalStateException("Activity definition not found: " + activity));
        String resolution = activityDefinition.getExpiresWithResolution();

        log.info("Expiring activity [{}] with resolution [{}] of workflow [{}]", activity, resolution, workflow);
        workflowService.completeActivity(activity.getId(), resolution, "Expired");
        return 1;
    }

    private Callable<Integer> buildCallable(Long activityId, Long clientId, Function<Long, Integer> function, BiFunction<Long, Exception, Integer> exceptionHandler) {
        return () -> {
            Lock lock = null;
            try {
                // this guarantees that there are no parallel activities executed for same client (and workflow essentially)
                lock = rwLockStripes.get(clientId);
                if (!lock.tryLock(30, TimeUnit.SECONDS)) {
                    log.warn("Failed to acquire lock for activity [{}] and client [{}]", activityId, clientId);
                    return 0;
                }
                return txTemplate.execute(status -> function.apply(activityId));
            } catch (OptimisticLockingFailureException e) {
                log.warn("Failed to acquire DB lock for activity [{}] and client [{}]", activityId, clientId);
                return 0;
            } catch (Exception e) {
                return txTemplate.execute(status -> exceptionHandler.apply(activityId, e));
            } finally {
                if (lock != null) {
                    lock.unlock();
                }
            }
        };
    }

    private class FailActivityOnException implements BiFunction<Long, Exception, Integer> {

        @Override
        public Integer apply(Long activityId, Exception e) {
            ActivityEntity activity = activityRepository.getRequired(activityId);
            log.error("Failed to process activity " + activity, e);
            String error = Throwables.getRootCause(e).getMessage();
            workflowService.failActivity(activity.getId(), error);
            log.warn("System activity [{}] of workflow [{}] failed: [{}]", activity, activity.getWorkflow(), error);
            return 0;
        }
    }

    private class FailTriggerOnException implements BiFunction<Long, Exception, Integer> {

        private Long triggerId;

        FailTriggerOnException(Long triggerId) {
            this.triggerId = triggerId;
        }

        @Override
        public Integer apply(Long activityId, Exception e) {
            TriggerEntity trigger = triggerRepository.getRequired(triggerId);
            log.error("Failed to process trigger " + trigger, e);
            String error = Throwables.getRootCause(e).getMessage();
            triggerService.failTrigger(trigger.getId(), error);
            log.warn("Trigger [{}] failed: [{}]", trigger, error);
            return 0;
        }
    }
}
