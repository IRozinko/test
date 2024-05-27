package fintech.task.event;

import fintech.task.model.Task;

public class TaskExpiredEvent extends AbstractTaskEvent {
    public TaskExpiredEvent(Task task) {
        super(task);
    }
}
