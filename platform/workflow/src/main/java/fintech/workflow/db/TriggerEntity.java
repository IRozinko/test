package fintech.workflow.db;

import fintech.db.BaseEntity;
import fintech.workflow.TriggerStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "trigger", schema = Entities.SCHEMA)
public class TriggerEntity extends BaseEntity {

    @Column(nullable = false)
    private Long workflowId;

    @Column(nullable = false)
    private Long activityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TriggerStatus status = TriggerStatus.WAITING;

    @Column(nullable = false)
    private String name;

    private String params;

    private String error;

    @Column(nullable = false)
    private LocalDateTime nextAttemptAt;

    public boolean isWaiting() {
        return this.status == TriggerStatus.WAITING;
    }

    public void completed() {
        this.status = TriggerStatus.COMPLETED;
    }

    public void failed(String error) {
        this.status = TriggerStatus.FAILED;
        this.error = error;
    }

    public void cancel() {
        this.status = TriggerStatus.CANCELLED;
    }
}
