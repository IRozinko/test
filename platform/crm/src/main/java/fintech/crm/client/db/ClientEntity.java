package fintech.crm.client.db;

import fintech.crm.client.Client;
import fintech.crm.client.ClientSegmentEmbeddable;
import fintech.crm.client.Gender;
import fintech.crm.contacts.db.EmailContactEntity;
import fintech.crm.db.Entities;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Getter
@Setter
@ToString(callSuper = true, exclude = {"phone", "email", "accountNumber", "documentNumber"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "client", schema = Entities.SCHEMA)
@DynamicUpdate
public class ClientEntity extends BaseEntity {

    @Column(nullable = false, unique = true, name = "client_number")
    private String number;

    private String phone;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<EmailContactEntity> email = newArrayList();

    private String firstName;
    private String secondFirstName;
    private String lastName;
    private String secondLastName;
    private String maidenName;
    private String documentNumber;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String title;

    @Column(columnDefinition = "DATE")
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private boolean acceptTerms;

    @Column(nullable = false)
    private boolean acceptMarketing;

    @Column(nullable = false)
    private boolean acceptVerification;

    @Column(nullable = false)
    private boolean acceptPrivacyPolicy;

    @Column(nullable = false)
    private boolean blockCommunication;

    @Column(nullable = false, name = "excluded_from_asnef")
    private boolean excludedFromASNEF;

    @Column(nullable = false)
    private boolean transferredToLoc;

    @OptimisticLock(excluded = true)
    @ElementCollection
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "client_attribute", joinColumns = @JoinColumn(name = "client_id"), schema = Entities.SCHEMA)
    private Map<String, String> attributes = new HashMap<>();

    @OptimisticLock(excluded = true)
    @ElementCollection
    @CollectionTable(name = "client_segment", joinColumns = @JoinColumn(name = "client_id"), schema = Entities.SCHEMA)
    private Set<ClientSegmentEmbeddable> segments = new HashSet<>();

    private String segmentsText;

    @Column(nullable = false)
    private boolean deleted;

    @Column(nullable = false)
    private String locale;

    public void addEmail(EmailContactEntity emailContact) {
        email.add(emailContact);
    }

    public Client toValueObject() {
        Client client = new Client();
        client.setId(this.id);
        client.setNumber(this.number);
        client.setPhone(this.phone);
        client.setEmail(this.email.stream().filter(EmailContactEntity::isPrimary).map(EmailContactEntity::getEmail).findFirst().orElse(null));
        client.setTitle(this.title);
        client.setFirstName(this.firstName);
        client.setSecondFirstName(this.secondFirstName);
        client.setLastName(this.lastName);
        client.setSecondLastName(this.secondLastName);
        client.setMaidenName(this.maidenName);
        client.setDocumentNumber(this.documentNumber);
        client.setGender(this.gender);
        client.setDateOfBirth(this.dateOfBirth);
        client.setAcceptTerms(this.acceptTerms);
        client.setAcceptMarketing(this.acceptMarketing);
        client.setAcceptVerification(this.acceptVerification);
        client.setAcceptPrivacyPolicy(this.acceptPrivacyPolicy);
        client.setBlockCommunication(this.blockCommunication);
        client.setExcludedFromASNEF(this.excludedFromASNEF);
        client.setTransferredToLoc(this.transferredToLoc);
        client.setAccountNumber(this.accountNumber);
        client.setAttributes(new HashMap<>(this.attributes));
        client.setCreatedAt(this.createdAt);
        client.setSegments(this.segments.stream().map(ClientSegmentEmbeddable::toValueObject).collect(Collectors.toSet()));
        client.setDeleted(this.deleted);
        client.setLocale(this.locale);
        return client;
    }
}
