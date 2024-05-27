package fintech.spain.alfa.product.activity;

import fintech.activity.ActivityService;
import fintech.activity.commands.AddActivityCommand;
import fintech.task.model.Task;
import fintech.task.model.TaskLog;
import fintech.task.event.TaskLogAddedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AddActivityOnTaskEvents {

    @Autowired
    private ActivityService activityService;

    @EventListener
    public void onTaskLog(TaskLogAddedEvent event) {
        TaskLog log = event.getTaskLog();
        Task task = event.getTask();
        if (StringUtils.isBlank(log.getComment())) {
            return;
        }
        AddActivityCommand command = new AddActivityCommand();
        command.setAction(task.getTaskType());
        command.setAgent(log.getAgent());
        command.setClientId(task.getClientId());
        command.setLoanId(task.getLoanId());
        command.setApplicationId(task.getApplicationId());
        command.setComments(log.getComment());
        command.setResolution(log.getResolution());
        command.setTopic("Task");
        command.setSource("Task");
        activityService.addActivity(command);
    }
}
