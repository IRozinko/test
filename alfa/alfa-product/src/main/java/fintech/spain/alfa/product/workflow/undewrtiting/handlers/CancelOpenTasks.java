package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.task.command.CancelTaskCommand;
import fintech.task.model.Task;
import fintech.task.model.TaskQuery;
import fintech.task.TaskService;
import fintech.task.model.TaskStatus;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class CancelOpenTasks implements ActivityListener {

    @Autowired
    private TaskService taskService;

    @Override
    public void handle(ActivityContext context) {
        taskService.findTasks(TaskQuery.byActivityId(context.getActivity().getId()))
            .stream()
            .filter((t) -> t.getStatus() == TaskStatus.OPEN)
            .forEach(this::cancelTask);
    }

    private void cancelTask(Task task) {
        CancelTaskCommand command = new CancelTaskCommand();
        command.setTaskId(task.getId());
        command.setReason("CancelledByWorkflow");
        taskService.cancelTask(command);
    }
}
