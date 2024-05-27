package fintech.task.model;

import lombok.Data;

@Data
public class TaskCount {

    private long tasksDue;

    public static TaskCount empty() {
        TaskCount empty = new TaskCount();
        return empty;
    }
}
