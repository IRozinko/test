package fintech.workflow.db;

import fintech.db.BaseEntity;
import fintech.workflow.Activity;
import fintech.workflow.ActivityStatus;
import fintech.workflow.Actor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true, of = {"name", "status", "actor"})
@Entity
@Table(name = "activity", schema = Entities.SCHEMA,
    indexes = {
        @Index(columnList = "workflow_id, name", name = "idx_activity_activity_name_uq", unique = true),
        @Index(columnList = "workflow_id", name = "idx_activity_workflow_id"),
        @Index(columnList = "nextAttemptAt, status, actor", name = "idx_activity_next_attempt_at"),
        @Index(columnList = "expiresAt, status", name = "idx_activity_expires_at"),
    })
public class ActivityEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "workflow_id")
    private WorkflowEntity workflow;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Actor actor;

    @Column(nullable = false)
    private long attempts = 0;

    private LocalDateTime completedAt;

    private LocalDateTime nextAttemptAt;

    private LocalDateTime expiresAt;

    private String error;

    private String resolution;
    private String resolutionDetail;

    private String uiState;

    public boolean isActive() {
        return this.status == ActivityStatus.ACTIVE;
    }

    public boolean isWaiting() {
        return this.status == ActivityStatus.WAITING;
    }

    public boolean isCancelled() {
        return this.status == ActivityStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return this.status == ActivityStatus.COMPLETED;
    }

    public Activity toValueObject() {
        Activity activity = new Activity();
        activity.setActor(this.getActor());
        activity.setName(this.getName());
        activity.setStatus(this.getStatus());
        activity.setResolution(this.getResolution());
        activity.setResolutionDetail(this.getResolutionDetail());
        activity.setWorkflowId(this.getWorkflow().getId());
        activity.setId(this.getId());
        activity.setNextAttemptAt(this.getNextAttemptAt());
        activity.setAttempts(this.getAttempts());
        activity.setCompletedAt(this.getCompletedAt());
        activity.setClientId(workflow.getClientId());
        activity.setWorkflowName(workflow.getName());
        activity.setUiState(this.uiState);
        return activity;
    }
}
