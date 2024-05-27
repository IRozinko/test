package fintech.presence.db;

import com.google.common.collect.Lists;
import fintech.db.BaseEntity;
import fintech.presence.OutboundLoad;
import fintech.presence.model.OutboundLoadStatus;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@ToString(callSuper = true)
@Table(name = "outbound_load", schema = Entities.SCHEMA, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"load_id", "service_id"})
})
public class OutboundLoadEntity extends BaseEntity {

    @Column(nullable = false, name = "load_id")
    private Integer loadId;

    @Column(nullable = false, name = "service_id")
    private Integer serviceId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboundLoadStatus status;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime addedAt;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "outboundLoad", cascade = CascadeType.ALL)
    private List<OutboundLoadRecordEntity> outboundLoadRecords = Lists.newArrayList();

    public OutboundLoad toValueObject() {
        OutboundLoad outboundLoad = new OutboundLoad();
        outboundLoad.setId(id);
        outboundLoad.setLoadId(loadId);
        outboundLoad.setServiceId(serviceId);
        outboundLoad.setStatus(status);
        outboundLoad.setAddedAt(addedAt);
        outboundLoad.setDescription(description);
        return outboundLoad;
    }
}
