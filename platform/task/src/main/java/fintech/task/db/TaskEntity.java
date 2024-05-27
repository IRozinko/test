package fintech.task.db;

import com.google.common.collect.ImmutableMap;
import fintech.db.BaseEntity;
import fintech.task.model.Task;
import fintech.task.model.TaskStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString(callSuper = true, of = {"taskType", "clientId", "status", "resolution"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "task", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_task_client_id"),
    @Index(columnList = "loanId", name = "idx_task_loan_id"),
    @Index(columnList = "applicationId", name = "idx_task_application_id"),
    @Index(columnList = "activityId", name = "idx_task_activity_id"),
    @Index(columnList = "workflowId", name = "idx_task_workflow_id"),
    @Index(columnList = "dueAt", name = "idx_task_due_at"),
    @Index(columnList = "expiresAt", name = "idx_task_expires_at")
})
public class TaskEntity extends BaseEntity {

    // This field means that task won't be assigned to anyone before it
    @Column(nullable = false)
    private LocalDateTime dueAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Long clientId;

    private Long applicationId;

    private Long loanId;

    private Long activityId;

    private Long workflowId;

    private Long parentTaskId;

    private Long installmentId;

    @Column(nullable = false, name = "task_type")
    private String taskType;

    @Column(nullable = false, name = "task_group")
    private String group;

    @Column(nullable = false)
    private Long priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String resolution;

    private String resolutionDetail;

    private String resolutionSubDetail;

    private String comment;

    private String agent;

    private LocalDateTime assignedAt;

    @Column(nullable = false)
    private Long timesPostponed = 0L;

    @Column(nullable = false)
    private Long timesReopened = 0L;

    @OptimisticLock(excluded = true)
    @ElementCollection
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "task_attribute", joinColumns = @JoinColumn(name = "task_id"), schema = Entities.SCHEMA)
    private Map<String, String> attributes = new HashMap<>();

    public Task toValueObject() {
        Task task = new Task();
        task.setId(id);
        task.setCreatedAt(createdAt);
        task.setExpiresAt(expiresAt);
        task.setDueAt(dueAt);
        task.setClientId(clientId);
        task.setApplicationId(applicationId);
        task.setLoanId(loanId);
        task.setActivityId(activityId);
        task.setParentTaskId(parentTaskId);
        task.setWorkflowId(workflowId);
        task.setInstallmentId(installmentId);
        task.setTaskType(taskType);
        task.setGroup(group);
        task.setPriority(priority);
        task.setStatus(status);
        task.setResolution(resolution);
        task.setAgent(agent);
        task.setTimesPostponed(timesPostponed);
        task.setTimesReopened(timesReopened);
        task.setAttributes(ImmutableMap.copyOf(attributes));
        return task;
    }
}
