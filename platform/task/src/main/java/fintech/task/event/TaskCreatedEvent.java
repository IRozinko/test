package fintech.task.event;

import fintech.task.model.Task;

public class TaskCreatedEvent extends AbstractTaskEvent {
    public TaskCreatedEvent(Task task) {
        super(task);
    }
}
