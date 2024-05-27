package fintech.spain.alfa.product.workflow.common;

import com.google.common.collect.ImmutableMap;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.task.TaskService;
import fintech.task.command.AddTaskCommand;
import fintech.task.command.ReopenTaskCommand;
import fintech.task.model.Task;
import fintech.task.model.TaskQuery;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskRegistry;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class CreateTask implements ActivityListener {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRegistry taskRegistry;

    @Autowired
    private SettingsService settingsService;

    private final String type;
    private final Long expiresInDays;

    public CreateTask(String type) {
        this(type, null);
    }

    public CreateTask(String type, Long expiresInDays) {
        this.type = type;
        this.expiresInDays = expiresInDays;
    }

    @Override
    public void handle(ActivityContext context) {

        TaskDefinition taskDefinition = taskRegistry.getDefinition(type);
        Long parentTaskId = null;
        if (taskDefinition.getDependedTask() != null) {
            parentTaskId = createParentTaskIfNotExists(taskDefinition.getDependedTask(), context);
        }

        List<Task> existingTasks = taskService.findTasks(TaskQuery.byActivityId(context.getActivity().getId()));
        if (!existingTasks.isEmpty()) {
            existingTasks.forEach((t) -> {
                ReopenTaskCommand command = new ReopenTaskCommand();
                command.setTaskId(t.getId());
                command.setReason("Activity reactivated");
                command.setExpiresAt(t.getExpiresAt());
                command.setDueAt(TimeMachine.now());
                command.setAttributes(ImmutableMap.copyOf(context.getWorkflow().getAttributes()));
                taskService.reopenTask(command);
            });
        } else {
            log.info("Adding task for activity [{}]", context.getActivity());
            AddTaskCommand command = new AddTaskCommand();
            command.setClientId(context.getWorkflow().getClientId());
            command.setType(type);
            command.setParentTaskId(parentTaskId);
            command.setExpiresAt(TimeMachine.now().plusDays(getExpirationDays()));
            command.setDueAt(TimeMachine.now().plusSeconds(context.getActivityDefinition().getInitialDelayInSeconds()));
            command.setActivityId(context.getActivity().getId());
            command.setWorkflowId(context.getWorkflow().getId());
            command.setLoanId(context.getWorkflow().getLoanId());
            command.setApplicationId(context.getWorkflow().getApplicationId());
            command.setAttributes(ImmutableMap.copyOf(context.getWorkflow().getAttributes()));
            taskService.addTask(command);
        }
    }

    private Long getExpirationDays() {
        if (this.expiresInDays == null) {
            AlfaSettings.TaskSettings settings = settingsService.getJson(AlfaSettings.LENDING_RULES_BASIC, AlfaSettings.TaskSettings.class);
            return (long) settings.getDefaultTaskExpirationInDays();
        }
        return this.expiresInDays;
    }

    private Long createParentTaskIfNotExists(String parentTask, ActivityContext context) {
        List<Task> existingTasks = taskService.findTasks(new TaskQuery().setWorkflowId(context.getWorkflow().getId()).setType(parentTask));
        if (existingTasks.isEmpty()) {
            log.info("Adding parent task [{}] for workflow [{}]", parentTask, context.getWorkflow());
            AddTaskCommand command = new AddTaskCommand();
            command.setClientId(context.getWorkflow().getClientId());
            command.setType(parentTask);
            command.setExpiresAt(TimeMachine.now().plusDays(getExpirationDays()));
            command.setDueAt(TimeMachine.now().plusSeconds(context.getActivityDefinition().getInitialDelayInSeconds()));
            command.setWorkflowId(context.getWorkflow().getId());
            command.setLoanId(context.getWorkflow().getLoanId());
            command.setApplicationId(context.getWorkflow().getApplicationId());
            command.setAttributes(ImmutableMap.copyOf(context.getWorkflow().getAttributes()));
            return taskService.addTask(command);
        } else {
            Validate.isTrue(existingTasks.size() == 1, "Parent task [{}] should be created only once", parentTask);
            if (!existingTasks.get(0).isOpen()) {
                ReopenTaskCommand command = new ReopenTaskCommand();
                command.setTaskId(existingTasks.get(0).getId());
                command.setReason("Child activity reactivated");
                command.setExpiresAt(existingTasks.get(0).getExpiresAt());
                command.setDueAt(TimeMachine.now());
                command.setAttributes(ImmutableMap.copyOf(context.getWorkflow().getAttributes()));
                taskService.reopenTask(command);
            }
            return existingTasks.get(0).getId();
        }

    }
}
