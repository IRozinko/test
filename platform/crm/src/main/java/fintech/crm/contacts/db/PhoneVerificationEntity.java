package fintech.crm.contacts.db;

import fintech.crm.client.db.ClientEntity;
import fintech.crm.db.Entities;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "phone_verification", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "client_id", name = "idx_phone_verification_client_id"),
})
public class PhoneVerificationEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "phone_contact_id")
    private PhoneContactEntity phoneContact;

    @Column(nullable = false)
    private String code;

    private boolean latest;

    private boolean verified;

    private LocalDateTime verifiedAt;

    private int attempts;
}
