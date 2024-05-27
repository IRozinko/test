package fintech.crm.contacts.db;

import fintech.crm.client.db.ClientEntity;
import fintech.crm.contacts.EmailContact;
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
@Table(name = "email_contact", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "client_id", name = "idx_email_contact_client_id"),
})
public class EmailContactEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(nullable = false)
    private String email;

    private boolean verified;

    private LocalDateTime verifiedAt;

    public EmailContact toValueObject() {
        EmailContact contact = new EmailContact();
        contact.setEmail(this.getEmail());
        contact.setId(this.getId());
        contact.setPrimary(this.isPrimary());
        contact.setClientId(this.client.getId());
        contact.setVerified(this.verified);
        contact.setVerifiedAt(this.verifiedAt);
        return contact;
    }
}
