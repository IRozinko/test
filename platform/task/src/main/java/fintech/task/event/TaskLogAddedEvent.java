package fintech.task.event;

import fintech.task.model.Task;
import fintech.task.model.TaskLog;
import lombok.Data;

@Data
public class TaskLogAddedEvent {

    private final Task task;
    private final TaskLog taskLog;

    public TaskLogAddedEvent(Task task, TaskLog taskLog) {
        this.task = task;
        this.taskLog = taskLog;
    }
}
