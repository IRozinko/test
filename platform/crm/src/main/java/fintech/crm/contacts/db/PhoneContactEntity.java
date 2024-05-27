package fintech.crm.contacts.db;

import fintech.crm.client.db.ClientEntity;
import fintech.crm.contacts.PhoneContact;
import fintech.crm.contacts.PhoneSource;
import fintech.crm.contacts.PhoneType;
import fintech.crm.db.Entities;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true, exclude = "localNumber")
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "phone_contact", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "client_id", name = "idx_phone_contact_client_id"),
})
public class PhoneContactEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(nullable = false)
    private boolean active;

    private LocalDate activeTill;

    @Column(nullable = false)
    private String localNumber;

    @Column(nullable = false)
    private String countryCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PhoneType phoneType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PhoneSource source;

    private boolean legalConsent;

    private boolean verified;

    private LocalDateTime verifiedAt;

    public PhoneContact toValueObject() {
        PhoneContact phoneContact = new PhoneContact();
        phoneContact.setId(this.getId());
        phoneContact.setClientId(this.getClient().getId());
        phoneContact.setCountryCode(this.getCountryCode());
        phoneContact.setLocalNumber(this.getLocalNumber());
        phoneContact.setPrimary(this.isPrimary());
        phoneContact.setPhoneType(this.getPhoneType());
        phoneContact.setVerified(this.verified);
        phoneContact.setVerifiedAt(this.verifiedAt);
        phoneContact.setActive(active);
        phoneContact.setActiveTill(activeTill);
        phoneContact.setSource(source);
        phoneContact.setLegalConsent(legalConsent);
        return phoneContact;
    }
}
