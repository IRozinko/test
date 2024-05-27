package fintech.task.event;

import fintech.task.model.Task;

public class TaskCompletedEvent extends AbstractTaskEvent {
    public TaskCompletedEvent(Task task) {
        super(task);
    }
}
