package fintech.task.model;

import fintech.TimeMachine;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskLog {

    public enum Operation {
        CREATED, ASSIGNED, POSTPONED, COMPLETED, REOPENED, CANCELLED
    }

    private Long taskId;
    private TaskLog.Operation operation;
    private String agent;
    private String reason;
    private String resolution;
    private String comment;
    private LocalDateTime createdAt = TimeMachine.now();
}
