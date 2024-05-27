package fintech.spain.callcenter.db;

import fintech.db.BaseEntity;
import fintech.spain.callcenter.Call;
import fintech.spain.callcenter.CallStatus;
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
import javax.persistence.UniqueConstraint;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "call", schema = Entities.SCHEMA, uniqueConstraints = {
    @UniqueConstraint(columnNames = { "provider_id", "client_id" })
})
public class CallEntity extends BaseEntity {

    @Column(nullable = false, name = "provider_id")
    private Long providerId;

    @Column(nullable = false, name = "client_id")
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallStatus status;

    public Call toValueObject() {
        Call call = new Call();
        call.setId(id);
        call.setClientId(clientId);
        call.setProviderId(providerId);
        call.setStatus(status);
        return call;
    }
}
