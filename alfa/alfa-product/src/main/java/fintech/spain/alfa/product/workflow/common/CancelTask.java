package fintech.spain.alfa.product.workflow.common;

import fintech.task.command.CancelTaskCommand;
import fintech.task.model.TaskQuery;
import fintech.task.TaskService;
import fintech.task.model.TaskStatus;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class CancelTask implements ActivityListener {

    private final String type;

    @Autowired
    private TaskService taskService;

    public CancelTask(String type) {
        this.type = type;
    }

    @Override
    public void handle(ActivityContext context) {
        taskService.findTasks(TaskQuery.byWorkflowId(context.getWorkflow().getId())).stream()
            .filter(task -> StringUtils.equals(type, task.getTaskType()) && task.getStatus() == TaskStatus.OPEN)
            .forEach(task -> {
                CancelTaskCommand command = new CancelTaskCommand();
                command.setTaskId(task.getId());
                command.setReason("CancelledByWorkflow");

                taskService.cancelTask(command);
            });
    }
}
