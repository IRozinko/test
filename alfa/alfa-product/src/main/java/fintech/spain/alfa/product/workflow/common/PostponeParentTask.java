package fintech.spain.alfa.product.workflow.common;

import fintech.task.TaskService;
import fintech.task.command.PostponeTaskCommand;
import fintech.task.model.Task;
import fintech.task.spi.TaskContext;
import fintech.task.spi.TaskListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PostponeParentTask implements TaskListener {

    @Autowired
    private TaskService taskService;

    private final String postponeResolution;

    public PostponeParentTask(String postponeResolution) {
        this.postponeResolution = postponeResolution;
    }

    @Override
    public void handle(TaskContext context) {
        Task task = context.getTask();
        if (task.getParentTaskId() != null) {
            PostponeTaskCommand command = new PostponeTaskCommand();
            command.setTaskId(task.getParentTaskId());
            command.setPostponeTo(task.getDueAt());
            command.setResolution(postponeResolution);
            taskService.postponeTask(command);
        }
    }
}
