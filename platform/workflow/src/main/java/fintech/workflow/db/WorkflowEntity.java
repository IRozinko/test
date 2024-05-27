package fintech.workflow.db;

import fintech.db.BaseEntity;
import fintech.workflow.ActivityStatus;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString(callSuper = true, of = {"name", "status", "clientId", "activities"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "workflow", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "loanId", name = "idx_workflow_loan_id"),
    @Index(columnList = "clientId", name = "idx_workflow_client_id"),
    @Index(columnList = "applicationId", name = "idx_workflow_application_id"),
})
public class WorkflowEntity extends BaseEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long clientId;

    @OptimisticLock(excluded = true)
    private Long loanId;

    private Long applicationId;

    @Column
    private Integer version;

    @Column
    private LocalDateTime completedAt;

    private String terminateReason;

    @NotAudited
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL)
    @OrderBy("id")
    private List<ActivityEntity> activities = new ArrayList<>();

    @OptimisticLock(excluded = true)
    @ElementCollection
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "workflow_attribute", joinColumns = @JoinColumn(name = "workflow_id"), schema = Entities.SCHEMA)
    private Map<String, String> attributes = new HashMap<>();

    private boolean suspended;

    @Column(name = "parent_workflow_id")
    private Long parentWorkflowId;

    public List<ActivityEntity> getActivitiesByStatus(ActivityStatus... statuses) {
        return activities.stream().filter((a) -> ArrayUtils.contains(statuses, a.getStatus())).collect(Collectors.toList());
    }

    public List<ActivityEntity> getActive() {
        return getActivitiesByStatus(ActivityStatus.ACTIVE);
    }


    public List<ActivityEntity> getWaiting() {
        return getActivitiesByStatus(ActivityStatus.WAITING);
    }


    public List<ActivityEntity> getCompleted() {
        return getActivitiesByStatus(ActivityStatus.COMPLETED);
    }

    public ActivityEntity getActivity(String activity) {
        Optional<ActivityEntity> first = activities.stream().filter(a -> activity.equals(a.getName())).findFirst();
        return first.orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activity));
    }

    public boolean isCompleted() {
        return this.status == WorkflowStatus.COMPLETED;
    }

    public boolean isActive() {
        return this.status == WorkflowStatus.ACTIVE;
    }

    public Workflow toValueObject() {
        Workflow wf = new Workflow();
        wf.setId(this.getId());
        wf.setParentWorkflowId(this.getParentWorkflowId());
        wf.setStatus(this.getStatus());
        wf.setClientId(this.getClientId());
        wf.setLoanId(this.getLoanId());
        wf.setName(this.getName());
        wf.setVersion(this.getVersion());
        wf.setApplicationId(this.getApplicationId());
        wf.setAttributes(new HashMap<>(this.attributes));
        wf.setActivities(this.getActivities().stream().map(ActivityEntity::toValueObject).collect(Collectors.toList()));
        wf.setCompletedAt(this.getCompletedAt());
        wf.setCreatedAt(this.getCreatedAt());
        wf.setTerminateReason(this.getTerminateReason());
        return wf;
    }

}
