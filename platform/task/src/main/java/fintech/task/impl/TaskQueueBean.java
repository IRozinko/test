package fintech.task.impl;


import com.querydsl.core.types.dsl.BooleanExpression;
import fintech.task.AgentService;
import fintech.task.TaskQueueService;
import fintech.task.TaskService;
import fintech.task.command.AssignTaskCommand;
import fintech.task.db.TaskEntity;
import fintech.task.db.TaskLogRepository;
import fintech.task.db.TaskRepository;
import fintech.task.model.Agent;
import fintech.task.model.Task;
import fintech.task.model.TaskCount;
import fintech.task.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static fintech.task.db.Entities.task;

@Slf4j
@Component
public class TaskQueueBean implements TaskQueueService {

    public static final int MAX_MINUTES_TO_WAIT_TASK_COMPLETION = 60 * 4;

    @Autowired
    private AgentService agentService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskLogRepository taskLogRepository;

    @Autowired
    private TaskService taskService;

    @Transactional
    @Override
    public Optional<Task> assignNextTask(String agentEmail, LocalDateTime when) {
        Optional<Agent> maybeAgent = agentService.findByEmail(agentEmail);
        if (!maybeAgent.isPresent() || maybeAgent.get().isDisabled()) {
            return Optional.empty();
        }
        Agent agent = maybeAgent.get();

        List<TaskEntity> tasks = findNextTasks(when, agent, 1);
        if (tasks.isEmpty()) {
            log.info("No tasks found to assign to agent [{}] at [{}]", agentEmail, when);
            return Optional.empty();
        }
        TaskEntity first = tasks.get(0);
        AssignTaskCommand command = new AssignTaskCommand();
        command.setAgent(agentEmail);
        command.setTaskId(first.getId());
        command.setWhen(when);
        log.info("Assigned to agent [{}] task via task queue: [{}]", agentEmail, first);
        taskService.assignTask(command);
        return Optional.of(taskService.get(first.getId()));
    }

    private List<TaskEntity> findNextTasks(LocalDateTime when, Agent agent, int size) {
        return taskRepository.findAll(taskPredicates(when, agent),
            new QPageRequest(0, size, task.priority.desc(), task.dueAt.asc(), task.id.asc())).getContent();
    }

    private BooleanExpression taskPredicates(LocalDateTime when, Agent agent) {
        // if all tasks then expression always return true. Could not get Expressions.TRUE to work...
        BooleanExpression taskTypeExpression = agent.getTaskTypes().contains("*") ?
            task.taskType.isNotNull() : task.taskType.in(agent.getTaskTypes());

        LocalDateTime assignedBefore = when.minusMinutes(MAX_MINUTES_TO_WAIT_TASK_COMPLETION);

        BooleanExpression expression = task.dueAt.before(when)
            .and(task.status.eq(TaskStatus.OPEN))
            .and(task.parentTaskId.isNull())
            .and(task.expiresAt.after(when))
            .and(task.agent.eq(agent.getEmail())
                .or(
                    task.agent.ne(agent.getEmail()).and(task.assignedAt.before(assignedBefore)).and(taskTypeExpression)
                )
                .or(
                    task.agent.isNull().and(taskTypeExpression)
                )
            );
        return expression;
    }

    @Transactional
    @Override
    public TaskCount count(String agentEmail, LocalDateTime when) {
        Optional<Agent> agent = agentService.findByEmail(agentEmail);
        if (!agent.isPresent() || agent.get().isDisabled()) {
            return TaskCount.empty();
        }
        long total = taskRepository.count(taskPredicates(when, agent.get()));

        TaskCount count = new TaskCount();
        count.setTasksDue(total);
        return count;
    }
}
