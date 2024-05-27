package fintech.workflow.db;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import fintech.db.BaseEntity;
import fintech.workflow.ActivityListener;
import fintech.workflow.ActivityListenerStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true, of = {"activityName", "activityStatus", "resolution"})
@Entity
@Table(name = "activity_listener", schema = Entities.SCHEMA,
    indexes = {
        @Index(columnList = "workflowName, workflowVersion, triggerName, activityName", name = "idx_activity_listener_trigger_activity_started"),
        @Index(columnList = "workflowName, workflowVersion, triggerName, activityName, resolution", name = "idx_activity_listener_trigger_activity_resolution_completed")
    })
@TypeDefs({
    @TypeDef(
        name = "string-array",
        typeClass = StringArrayType.class
    )
})
public class ActivityListenerEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String workflowName;

    @Column(nullable = false)
    private String activityName;
    @Column(nullable = false)
    private String triggerName;
    private String resolution;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityListenerStatus activityStatus;

    @Column
    private Integer delaySec;
    private Boolean fromMidnight;

    @Column(nullable = false)
    private Integer workflowVersion;

    @Type(type = "string-array")
    @Column(
        name = "params",
        columnDefinition = "text[]"
    )
    private String[] params;

    public ActivityListener toValueObject() {
        ActivityListener activity = new ActivityListener();
        activity.setId(this.getId());
        activity.setName(this.getName());
        activity.setStatus(this.getActivityStatus());
        activity.setResolution(this.getResolution());
        activity.setWorkflowName(this.getWorkflowName());
        activity.setActivityName(this.getActivityName());
        activity.setTriggerName(this.getTriggerName());
        return activity;
    }
}
