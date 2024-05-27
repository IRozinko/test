package fintech.task.event;

import fintech.task.model.Task;

public class TaskReopenedEvent extends AbstractTaskEvent {
    public TaskReopenedEvent(Task task) {
        super(task);
    }
}
