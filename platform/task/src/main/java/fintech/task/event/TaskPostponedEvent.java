package fintech.task.event;

import fintech.task.model.Task;

public class TaskPostponedEvent extends AbstractTaskEvent {
    public TaskPostponedEvent(Task task) {
        super(task);
    }
}
