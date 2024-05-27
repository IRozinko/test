package fintech.task.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Predicate;
import fintech.task.TaskService;
import fintech.task.command.*;
import fintech.task.db.Entities;
import fintech.task.db.TaskEntity;
import fintech.task.db.TaskLogEntity;
import fintech.task.db.TaskLogRepository;
import fintech.task.db.TaskRepository;
import fintech.task.event.*;
import fintech.task.model.Task;
import fintech.task.model.TaskLog;
import fintech.task.model.TaskQuery;
import fintech.task.model.TaskStatus;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskListener;
import fintech.task.spi.TaskRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.allOf;

@Slf4j
@Transactional
@Component
public class TaskServiceBean implements TaskService {

    public static final String AGENT_FOR_EXPIRED_CASE = "SYSTEM";

    private final TaskRepository taskRepository;
    private final TaskRegistry taskRegistry;
    private final ApplicationContext applicationContext;
    private final TaskLogRepository taskLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public TaskServiceBean(TaskRepository taskRepository, TaskRegistry taskRegistry, ApplicationContext applicationContext,
                           TaskLogRepository taskLogRepository, ApplicationEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.taskRegistry = taskRegistry;
        this.applicationContext = applicationContext;
        this.taskLogRepository = taskLogRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Long addTask(AddTaskCommand command) {
        log.info("Adding task {}", command);
        validateAddTask(command);
        TaskDefinition definition = taskRegistry.getDefinition(command.getType());


        TaskEntity entity = new TaskEntity();
        entity.setClientId(command.getClientId());
        entity.setApplicationId(command.getApplicationId());
        entity.setLoanId(command.getLoanId());
        entity.setExpiresAt(command.getExpiresAt());
        entity.setDueAt(command.getDueAt());
        entity.setStatus(TaskStatus.OPEN);
        entity.setTaskType(command.getType());
        entity.setActivityId(command.getActivityId());
        entity.setWorkflowId(command.getWorkflowId());
        entity.setParentTaskId(command.getParentTaskId());
        entity.setInstallmentId(command.getInstallmentId());
        entity.setAttributes(ImmutableMap.copyOf(command.getAttributes()));


        entity.setGroup(definition.getGroup());
        entity.setTaskType(definition.getType());
        entity.setPriority(definition.getPriority());
        Long taskId = taskRepository.saveAndFlush(entity).getId();

        addLog(entity, TaskLog.Operation.CREATED, null);

        eventPublisher.publishEvent(new TaskCreatedEvent(entity.toValueObject()));
        if (command.getParentTaskId() != null) {
            TaskEntity parent = taskRepository.getRequired(command.getParentTaskId());
            if (parent.getAgent() != null)
                assignTask(new AssignTaskCommand().setAgent(parent.getAgent()).setTaskId(taskId).setComment("Autoassign subtask"));
        }
        return taskId;
    }

    @Override
    public void assignTask(AssignTaskCommand command) {
        log.info("Assigning task: [{}]", command);
        TaskEntity task = taskRepository.getRequired(command.getTaskId());

        if (command.getAgent().equals(task.getAgent())) {
            long minutesAssigned = ChronoUnit.MINUTES.between(task.getAssignedAt(), command.getWhen());
            if (minutesAssigned <= TaskQueueBean.MAX_MINUTES_TO_WAIT_TASK_COMPLETION) {
                // this prevents assigning task to the same agent too frequently
                log.info("Task already assigned to the same agent (minutes assigned [{}]): [{}]", minutesAssigned, command);
                return;
            }
        }
        task.setAgent(command.getAgent());
        task.setAssignedAt(command.getWhen());
        if (!StringUtils.isBlank(command.getComment())) {
            task.setComment(command.getComment());
        }
        addLog(task, null, TaskLog.Operation.ASSIGNED, null);
        eventPublisher.publishEvent(new TaskAssignedEvent(task.toValueObject()));

        taskRepository.findByParentTaskId(command.getTaskId()).forEach(d -> {
            assignTask(new AssignTaskCommand().setAgent(command.getAgent()).setTaskId(d.getId()).setComment("Autoassign subtask"));
        });

    }

    private void validateAddTask(AddTaskCommand command) {
        Validate.notNull(command.getClientId(), "Client ID required");
        Validate.notNull(command.getDueAt(), "Task due required");
        Validate.notNull(command.getExpiresAt(), "Task expiration required");
        Validate.notNull(command.getType(), "Task type required");
        TaskDefinition definition = taskRegistry.getDefinition(command.getType());
        Validate.isTrue(definition.getDependedTask() == null || command.getParentTaskId() != null);
        if (command.getParentTaskId() != null) {
            Validate.notNull(definition.getDependedTask(), "Depended task is required in definition");
            Task createdParent = taskRepository.getRequired(command.getParentTaskId()).toValueObject();
            Validate.isTrue(createdParent.getStatus() == TaskStatus.OPEN, "Created parent task [%s] should be OPEN", createdParent);
            Validate.isTrue(createdParent.getTaskType().equals(definition.getDependedTask()), "Depended task definition [%s] should be equal to created one [%s]", definition.getDependedTask(), createdParent.getTaskType());
        }
    }

    private void addLog(TaskEntity task, TaskLog.Operation operation, String reason) {
        addLog(task, task.getComment(), operation, reason);
    }

    private void addLog(TaskEntity task, String comment, TaskLog.Operation operation, String reason) {
        TaskLogEntity log = new TaskLogEntity();
        log.setAgent(task.getAgent());
        log.setTaskId(task.getId());
        log.setDueAt(task.getDueAt());
        log.setExpiresAt(task.getExpiresAt());
        log.setOperation(operation);
        log.setReason(reason);
        log.setResolution(task.getResolution());
        log.setResolutionDetail(task.getResolutionDetail());
        log.setResolutionSubDetail(task.getResolutionSubDetail());
        log.setComment(comment);
        taskLogRepository.saveAndFlush(log);
        eventPublisher.publishEvent(new TaskLogAddedEvent(task.toValueObject(), log.toValueObject()));
    }

    @Override
    public Task get(Long id) {
        TaskEntity entity = taskRepository.getRequired(id);
        return entity.toValueObject();
    }

    @Override
    public List<Task> findTasks(TaskQuery query) {
        List<TaskEntity> entities = taskRepository.findAll(allOf(toPredicates(query)), Entities.task.createdAt.asc());
        return entities.stream().map(TaskEntity::toValueObject).collect(Collectors.toList());
    }

    private List<Predicate> toPredicates(TaskQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getActivityId() != null) {
            predicates.add(Entities.task.activityId.eq(query.getActivityId()));
        }
        if (query.getWorkflowId() != null) {
            predicates.add(Entities.task.workflowId.eq(query.getWorkflowId()));
        }
        if (query.getClientId() != null) {
            predicates.add(Entities.task.clientId.eq(query.getClientId()));
        }
        if (query.getLoanId() != null) {
            predicates.add(Entities.task.loanId.eq(query.getLoanId()));
        }
        if (query.getType() != null) {
            predicates.add(Entities.task.taskType.eq(query.getType()));
        }
        if (query.getResolution() != null) {
            predicates.add(Entities.task.resolution.eq(query.getResolution()));
        }
        if (query.getParentTaskId() != null) {
            predicates.add(Entities.task.parentTaskId.eq(query.getParentTaskId()));
        }
        return predicates;
    }

    @Override
    public void postponeTask(PostponeTaskCommand command) {
        log.info("Postponing task {}", command);

        TaskEntity task = taskRepository.getRequired(command.getTaskId());
        TaskDefinition definition = taskRegistry.getDefinition(task.getTaskType());

        Validate.isTrue(task.getStatus() == TaskStatus.OPEN, "Task is not open: %s", task);
        Validate.isTrue(definition.getResolutions().contains(command.getResolution()), "Unknown task resolution [%s], available [%s]", command.getResolution(), definition.getResolutions());
        Validate.isTrue(definition.getResolutionsDefinition(command.getResolution()).isPostpone(), "Resolution does not allow postpone: [%s]", command.getResolution());

        task.setDueAt(command.getPostponeTo());
        task.setTimesPostponed(task.getTimesPostponed() + 1);
        task.setResolution(command.getResolution());
        task.setResolutionDetail(command.getResolutionDetail());
        task.setResolutionSubDetail(command.getResolutionSubDetail());
        task.setComment(command.getComment());
        command.getExpiresAt().ifPresent(task::setExpiresAt);
        // postponed tasks should have lower priority so that other unprocessed tasks are in front
        task.setPriority(definition.getPriorityAfterPostpone());

        addLog(task, TaskLog.Operation.POSTPONED, command.getResolution());

        runTaskCompletedListeners(task, definition, command.getResolution());

        eventPublisher.publishEvent(new TaskPostponedEvent(task.toValueObject()));
    }

    @Override
    public void completeTask(CompleteTaskCommand command) {
        log.info("Completing task {}", command);

        final String resolution = command.getResolution();

        TaskEntity task = taskRepository.getRequired(command.getTaskId());
        TaskDefinition definition = taskRegistry.getDefinition(task.getTaskType());

        Validate.isTrue(task.getStatus() == TaskStatus.OPEN, "Task is not open: %s", task);
        Validate.isTrue(definition.getResolutions().contains(resolution), "Unknown task resolution [%s], available [%s]", resolution, definition.getResolutions());
        Validate.isTrue(!definition.getResolutionsDefinition(command.getResolution()).isPostpone(), "Postponing resolution does not allow task completion: [%s]", command.getResolution());

        taskRepository.findByParentTaskId(command.getTaskId()).forEach(t -> Validate.isTrue(t.getStatus() != TaskStatus.OPEN, "Children task is OPEN: [%s]", t));

        task.setStatus(TaskStatus.COMPLETED);
        task.setResolution(command.getResolution());
        task.setResolutionDetail(command.getResolutionDetail());
        task.setResolutionSubDetail(command.getResolutionSubDetail());
        task.setComment(command.getComment());
        addLog(task, TaskLog.Operation.COMPLETED, resolution);

        runTaskCompletedListeners(task, definition, resolution);

        eventPublisher.publishEvent(new TaskCompletedEvent(task.toValueObject()));
    }

    private void runTaskCompletedListeners(TaskEntity task, TaskDefinition definition, String resolution) {
        Task valueObject = task.toValueObject();
        Optional.ofNullable(definition.getResolutionsDefinition(resolution))
            .ifPresent(d -> {
                d.getOnCompletedListeners().forEach((l) -> {
                    log.info("Executing task {} listener {}", task, l.getListenerClass());
                    TaskListener listener = applicationContext.getBean(l.getListenerClass(), l.getArgs());
                    listener.handle(new TaskContextImpl(valueObject));
                });
            });
    }

    @Override
    public void cancelTask(CancelTaskCommand command) {
        log.info("Canceling task {}", command);
        TaskEntity task = taskRepository.getRequired(command.getTaskId());
        Validate.isTrue(task.getStatus() == TaskStatus.OPEN, "Task is not open: %s", task);

        task.setStatus(TaskStatus.CANCELLED);
        task.setResolution(command.getReason());
        addLog(task, TaskLog.Operation.CANCELLED, command.getReason());
        eventPublisher.publishEvent(new TaskCancelledEvent(task.toValueObject()));

        taskRepository.findByParentTaskId(command.getTaskId()).stream()
            .filter(t -> t.getStatus() == TaskStatus.OPEN)
            .forEach(t -> cancelTask(new CancelTaskCommand(t.getId(), command.getReason())));
    }

    @Override
    public void reopenTask(ReopenTaskCommand command) {
        log.info("Reopening task {}", command);
        TaskEntity task = taskRepository.getRequired(command.getTaskId());
        Validate.isTrue(task.getStatus() != TaskStatus.OPEN, "Task is already open: %s", task);
        task.setStatus(TaskStatus.OPEN);
        task.setResolution(null);
        task.setDueAt(command.getDueAt());
        task.setExpiresAt(command.getExpiresAt());
        task.setTimesReopened(task.getTimesReopened() + 1);
        task.setAttributes(ImmutableMap.copyOf(command.getAttributes()));
        addLog(task, TaskLog.Operation.REOPENED, command.getReason());
        eventPublisher.publishEvent(new TaskReopenedEvent(task.toValueObject()));
    }

    @Override
    public void expireTask(ExpireTaskCommand command) {
        TaskEntity task = taskRepository.getRequired(command.getTaskId());

        TaskDefinition definition = taskRegistry.getDefinition(task.getTaskType());

        Validate.isTrue(task.getStatus() == TaskStatus.OPEN, "Task is not open: %s", task);
        String resolution = definition.getDefaultExpireResolution();

        log.info("Expiring task with type [{}] and id [{}], resolution [{}]", task.getTaskType(), task.getId(), resolution);

        task.setStatus(TaskStatus.COMPLETED);
        task.setResolution(resolution);
        task.setAgent(AGENT_FOR_EXPIRED_CASE);
        addLog(task, TaskLog.Operation.COMPLETED, resolution);

        runTaskCompletedListeners(task, definition, resolution);
        eventPublisher.publishEvent(new TaskExpiredEvent(task.toValueObject()));

        taskRepository.findByParentTaskId(command.getTaskId()).stream()
            .filter(t -> t.getStatus() == TaskStatus.OPEN)
            .forEach(t -> expireTask(new ExpireTaskCommand(t.getId())));
    }

    @Override
    public void addTaskAttributes(AddTaskAttributesCommand command) {
        log.info("Adding task {} attributes {}", command.getTaskId(), command.getAttributes());

        TaskEntity task = taskRepository.getRequired(command.getTaskId());
        task.getAttributes().putAll(command.getAttributes());
    }
}
