package fintech.task.event;

import fintech.task.model.Task;

public abstract class AbstractTaskEvent {

    private final Task task;

    public AbstractTaskEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
