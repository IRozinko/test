package fintech.spain.alfa.product.workflow.common;

import fintech.task.TaskService;
import fintech.task.command.CompleteTaskCommand;
import fintech.task.model.TaskQuery;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class CompleteTask implements ActivityListener {

    @Autowired
    private TaskService taskService;

    private final String type;
    private final String resolution;

    public CompleteTask(String type, String resolution) {
        this.type = type;
        this.resolution = resolution;
    }

    @Override
    public void handle(ActivityContext context) {
        taskService.findTasks(new TaskQuery().setWorkflowId(context.getWorkflow().getId()).setType(type))
            .forEach(t -> {
                    if (t.isOpen()) {
                        taskService.completeTask(new CompleteTaskCommand().setTaskId(t.getId()).setResolution(resolution));
                    }
                }
            );
    }

}
