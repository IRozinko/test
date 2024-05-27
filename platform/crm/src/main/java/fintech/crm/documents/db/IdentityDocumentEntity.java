package fintech.crm.documents.db;

import fintech.crm.client.db.ClientEntity;
import fintech.crm.country.db.CountryEntity;
import fintech.crm.db.Entities;
import fintech.crm.documents.IdentityDocument;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Getter
@Setter
@ToString(callSuper = true, exclude = "number")
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "identity_document", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "client_id", name = "idx_identity_document_client_id"),
})
public class IdentityDocumentEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Column(nullable = false)
    private String number;

    @Column(name = "document_type", nullable = false)
    private String type;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(columnDefinition = "DATE")
    private LocalDate issuedAt;

    @Column
    private String issuedBy;

    @Column(columnDefinition = "DATE")
    private LocalDate expiresAt;

    @Audited(targetAuditMode = NOT_AUDITED)
    @OneToOne
    @JoinColumn(name = "nationality_id", referencedColumnName = "id")
    private CountryEntity nationality;

    public IdentityDocument toValueObject() {
        IdentityDocument vo = new IdentityDocument();
        vo.setId(this.id);
        vo.setClientId(this.client.getId());
        vo.setType(this.type);
        vo.setNumber(this.number);
        vo.setNationality(this.nationality != null ? this.nationality.toValueObject() : null);
        return vo;
    }

}
