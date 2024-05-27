package fintech.spain.alfa.product.workflow.common;

import fintech.task.TaskService;
import fintech.task.command.CancelTaskCommand;
import fintech.task.model.TaskQuery;
import fintech.task.model.TaskStatus;
import fintech.workflow.spi.WorkflowListener;
import fintech.workflow.spi.WorkflowListenerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class CancelWorkflowTasksWorkflowListener implements WorkflowListener {

    @Autowired
    private TaskService taskService;
     
    @Override
    public void handle(WorkflowListenerContext context) {
        taskService.findTasks(TaskQuery.byWorkflowId(context.getWorkflow().getId())).stream()
            .filter(task -> task.getStatus() == TaskStatus.OPEN)
            .forEach(task -> {
                CancelTaskCommand command = new CancelTaskCommand();
                command.setTaskId(task.getId());
                command.setReason("CancelledByWorkflow");
                taskService.cancelTask(command);
            });
    }
}
