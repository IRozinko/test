package fintech.risk.checklist.db;

import fintech.db.BaseEntity;
import fintech.risk.checklist.CheckListEntry;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "checklist_entry", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "type", name = "idx_checklist_entry_type"),
})
public class CheckListEntryEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "type", referencedColumnName = "type")
    private CheckListTypeEntity type;

    @Column(nullable = false)
    private String value1;

    private String comment;

    public CheckListEntry toValueObject() {
        CheckListEntry entry = new CheckListEntry();
        entry.setComment(comment);
        entry.setId(getId());
        entry.setValue1(value1);
        entry.setType(type);
        return entry;
    }

}
