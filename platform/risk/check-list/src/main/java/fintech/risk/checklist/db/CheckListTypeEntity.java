package fintech.risk.checklist.db;

import fintech.db.BaseEntity;
import fintech.risk.checklist.model.CheckListAction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "checklist_type", schema = Entities.SCHEMA)
public class CheckListTypeEntity extends BaseEntity implements Serializable {

    @Column(nullable = false, unique = true)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckListAction action;

}
