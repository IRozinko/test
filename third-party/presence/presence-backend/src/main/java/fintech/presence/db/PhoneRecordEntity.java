package fintech.presence.db;

import fintech.db.BaseEntity;
import fintech.presence.PhoneRecord;
import fintech.presence.model.PhoneDescription;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@ToString(callSuper = true, exclude = {"outboundLoadRecord"})
@Table(name = "phone_record", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "outbound_load_record_id", name = "idx_phone_record_outbound_load_record_id")
})
public class PhoneRecordEntity extends BaseEntity {

    @Column(nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhoneDescription description;

    @ManyToOne
    @JoinColumn(name = "outbound_load_record_id")
    private OutboundLoadRecordEntity outboundLoadRecord;

    public PhoneRecord toValueObject() {
        PhoneRecord phoneRecord = new PhoneRecord();
        phoneRecord.setNumber(number);
        phoneRecord.setDescription(description);
        return phoneRecord;
    }
}
