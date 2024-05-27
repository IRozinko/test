package fintech.task.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@ToString(of = {"id", "taskType", "clientId", "status", "resolution", "loanId", "applicationId", "parentTaskId"})
public class Task {

    private Long id;
    private LocalDateTime createdAt;
    private String agent;
    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private Long activityId;
    private Long workflowId;
    private Long parentTaskId;
    private Long installmentId;
    private String taskType;
    private String group;
    private Long priority;
    private TaskStatus status;
    private String resolution;
    private LocalDateTime dueAt;
    private LocalDateTime expiresAt;
    private Long timesPostponed;
    private Long timesReopened;
    private Map<String, String> attributes = new HashMap<>();

    public boolean isOpen() {
        return this.status == TaskStatus.OPEN;
    }

    public boolean isCompleted() {
        return this.status == TaskStatus.COMPLETED;
    }
}
