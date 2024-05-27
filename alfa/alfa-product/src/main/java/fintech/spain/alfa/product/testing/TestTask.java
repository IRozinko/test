package fintech.spain.alfa.product.testing;

import fintech.TimeMachine;
import fintech.task.TaskService;
import fintech.task.command.AssignTaskCommand;
import fintech.task.command.CompleteTaskCommand;
import fintech.task.command.PostponeTaskCommand;
import fintech.task.model.Task;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskRegistry;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Accessors(chain = true)
public class TestTask {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRegistry taskRegistry;

    private final Long taskId;

    public TestTask(Long taskId) {
        this.taskId = taskId;
    }

    public Task getTask() {
        return taskService.get(taskId);
    }

    public TestTask complete(String resolution) {
        AssignTaskCommand assignTaskCommand = new AssignTaskCommand();
        assignTaskCommand.setTaskId(this.taskId);
        assignTaskCommand.setAgent("bo:testagent");
        taskService.assignTask(assignTaskCommand);

        TaskDefinition.TaskResolutionDefinition taskDefinition = taskRegistry.getDefinition(getTask().getTaskType()).getResolutionsDefinition(resolution);
        if (taskDefinition.isPostpone()) {
            taskService.postponeTask(new PostponeTaskCommand()
                .setTaskId(taskId)
                .setResolution(resolution)
                .setPostponeTo(TimeMachine.now().plusDays(1))
            );
        } else {
            taskService.completeTask(new CompleteTaskCommand()
                .setTaskId(taskId)
                .setResolution(resolution)
            );
        }

        return this;
    }
}
