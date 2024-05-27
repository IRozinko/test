package fintech.task.event;

import fintech.task.model.Task;

public class TaskAssignedEvent extends AbstractTaskEvent {
    public TaskAssignedEvent(Task task) {
        super(task);
    }
}
