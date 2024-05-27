package fintech.spain.alfa.product.workflow.common;


import fintech.task.model.Task;
import fintech.task.spi.TaskContext;
import fintech.task.spi.TaskListener;
import fintech.workflow.Activity;
import fintech.workflow.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CompleteActivity implements TaskListener {

    private final String activityResolution;

    @Autowired
    protected WorkflowService workflowService;

    public CompleteActivity(String activityResolution) {
        this.activityResolution = activityResolution;
    }

    @Override
    public void handle(TaskContext context) {
        Activity activity = workflowService.getActivity(context.getTask().getActivityId());
        Task task = context.getTask();
        log.info("Task [{}] of activity [{}] completed with resolution [{}] by agent [{}]", task.getTaskType(), activity, task.getResolution(), task.getAgent());
        String reason = task.getResolution();
        workflowService.completeActivity(activity.getId(), activityResolution, reason);
    }
}
