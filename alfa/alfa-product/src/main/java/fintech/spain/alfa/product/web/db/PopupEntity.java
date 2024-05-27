package fintech.spain.alfa.product.web.db;

import fintech.db.BaseEntity;
import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.product.web.model.PopupResolution;
import fintech.spain.alfa.product.web.model.PopupType;
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
@Entity
@ToString(callSuper = true)
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "popup", schema = Entities.SCHEMA,
    indexes = {
        @Index(columnList = "client_id", name = "idx_popup_client_id"),
        @Index(columnList = "client_id,resolution", name = "idx_popup_client_id_resolution"),
        @Index(columnList = "client_id,popup_type", name = "idx_popup_client_id_popup_type"),
    }
)
public class PopupEntity extends BaseEntity {

    @Column(nullable = false, name = "client_id")
    private long clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "popup_type")
    private PopupType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PopupResolution resolution;

    private LocalDateTime resolvedAt;

    private LocalDateTime validUntil;

    @OptimisticLock(excluded = true)
    @ElementCollection
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "popup_attribute", joinColumns = @JoinColumn(name = "popup_id"), schema = Entities.SCHEMA)
    private Map<String, String> attributes = new HashMap<>();
}
