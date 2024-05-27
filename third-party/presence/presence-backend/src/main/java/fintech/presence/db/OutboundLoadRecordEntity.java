package fintech.presence.db;

import com.google.common.collect.Lists;
import fintech.db.BaseEntity;
import fintech.presence.OutboundLoadRecord;
import fintech.presence.model.OutboundLoadRecordStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@ToString(callSuper = true, exclude = {"outboundLoad"})
@Table(name = "outbound_load_record", schema = Entities.SCHEMA, uniqueConstraints = {
    @UniqueConstraint(columnNames = { "source_id", "outbound_load_id" })
})
public class OutboundLoadRecordEntity extends BaseEntity {

    @Column(nullable = false, name = "source_id")
    private Integer sourceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboundLoadRecordStatus status;

    @Column
    private Integer qualificationCode;

    @ManyToOne
    @JoinColumn(name = "outbound_load_id")
    private OutboundLoadEntity outboundLoad;

    @OneToMany(mappedBy = "outboundLoadRecord", cascade = CascadeType.ALL)
    private List<PhoneRecordEntity> phoneRecords = Lists.newArrayList();

    public OutboundLoadRecord toValueObject() {
        OutboundLoadRecord outboundLoadRecord = new OutboundLoadRecord();
        outboundLoadRecord.setId(id);
        outboundLoadRecord.setName(name);
        outboundLoadRecord.setPhoneRecords(phoneRecords.stream().map(PhoneRecordEntity::toValueObject).collect(Collectors.toList()));
        outboundLoadRecord.setQualificationCode(qualificationCode);
        outboundLoadRecord.setSourceId(sourceId);
        outboundLoadRecord.setStatus(status);
        outboundLoadRecord.setOutboundLoad(outboundLoad.toValueObject());
        return outboundLoadRecord;
    }
}
