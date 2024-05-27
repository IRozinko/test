package fintech.payments.db;

import fintech.db.BaseEntity;
import fintech.payments.model.InstitutionAccount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "institution_account", schema = Entities.SCHEMA, indexes = {})
public class InstitutionAccountEntity extends BaseEntity {

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountingAccountCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "institution_id")
    private InstitutionEntity institution;

    @Column(nullable = false, name = "is_primary")
    private boolean primary;

    public InstitutionAccount toValueObject() {
        InstitutionAccount val = new InstitutionAccount();
        val.setId(this.id);
        val.setAccountNumber(this.accountNumber);
        val.setInstitutionId(this.institution.getId());
        val.setAccountingAccountCode(this.accountingAccountCode);
        val.setPrimary(this.primary);
        return val;
    }
}
