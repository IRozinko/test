package fintech.task.db;

import fintech.PojoUtils;
import fintech.db.BaseEntity;
import fintech.task.model.TaskLog;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "log", schema = Entities.SCHEMA)
public class TaskLogEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskLog.Operation operation;

    private String agent;

    @Column(nullable = false)
    private Long taskId;

    @Column(nullable = false)
    private LocalDateTime dueAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private String reason;

    private String resolution;

    private String resolutionDetail;

    private String resolutionSubDetail;

    private String comment;

    public TaskLog toValueObject() {
        TaskLog value = new TaskLog();
        PojoUtils.copyProperties(value, this);
        return value;
    }
}
