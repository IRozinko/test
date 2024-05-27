package fintech.task.event;

import fintech.task.model.Task;

public class TaskCancelledEvent extends AbstractTaskEvent {
    public TaskCancelledEvent(Task task) {
        super(task);
    }
}
