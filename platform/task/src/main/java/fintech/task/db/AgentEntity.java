package fintech.task.db;

import fintech.ExtraStringUtils;
import fintech.db.BaseEntity;
import fintech.task.model.Agent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "agent", schema = Entities.SCHEMA)
public class AgentEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    private String taskTypes;

    @Column(nullable = false)
    private boolean disabled;

    public Agent toValueObject() {
        Agent val = new Agent();
        val.setId(this.id);
        val.setEmail(this.email);
        val.setTaskTypes(ExtraStringUtils.splitCommaSeparatedList(taskTypes));
        val.setDisabled(this.disabled);
        return val;
    }
}
