package fintech.workflow;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString(of = {"id", "name", "status", "workflowId"})
public class Activity {

    private Long id;
    private Long workflowId;
    private String workflowName;
    private Long clientId;
    private String name;
    private ActivityStatus status;
    private String resolution;
    private String resolutionDetail;
    private Actor actor;
    private String uiState;
    private LocalDateTime nextAttemptAt;
    private Long attempts;
    private LocalDateTime completedAt;

    public boolean isActive() {
        return this.status == ActivityStatus.ACTIVE;
    }
}
