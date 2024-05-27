package fintech.spain.consents.db;

import fintech.db.BaseEntity;
import fintech.spain.consents.model.Consent;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "consent", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_consent_client_id")
})
public class ConsentEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private boolean accepted;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime changedAt;

    public Consent toValueObject() {
        Consent consent = new Consent();
        consent.setClientId(this.clientId);
        consent.setName(this.name);
        consent.setVersion(this.version);
        consent.setAccepted(this.accepted);
        consent.setSource(this.source);
        consent.setChangedAt(this.changedAt);
        return consent;
    }

}
