package fintech.payments.db;

import fintech.db.BaseEntity;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementStatus;
import fintech.payments.model.DisbursementStatusDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static fintech.BigDecimalUtils.amount;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "disbursement", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_disbursement_client_id"),
    @Index(columnList = "loanId", name = "idx_disbursement_loan_id"),
    @Index(columnList = "institutionId", name = "idx_disbursement_institution_id"),
    @Index(columnList = "institutionAccountId", name = "idx_disbursement_institution_account_id"),
})
public class DisbursementEntity extends BaseEntity {

    @Column(nullable = false)
    private String disbursementType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisbursementStatus status = DisbursementStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisbursementStatusDetail statusDetail = DisbursementStatusDetail.PENDING;

    @Column(nullable = false)
    private BigDecimal amount = amount(0);

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate valueDate;

    @Column(nullable = false, unique = true)
    private String reference;

    private Long clientId;

    private Long loanId;

    private Long applicationId;

    @Column(nullable = false)
    private Long institutionId;

    private Long institutionAccountId;

    private LocalDateTime exportedAt;

    private LocalDateTime settledAt;

    private String exportedFileName;

    private Long exportedCloudFileId;

    private String error;

    private boolean apiExport;

    public Disbursement toValueObject() {
        Disbursement val = new Disbursement();
        val.setId(this.id);
        val.setDisbursementType(this.disbursementType);
        val.setClientId(this.clientId);
        val.setLoanId(this.loanId);
        val.setApplicationId(this.applicationId);
        val.setInstitutionId(this.institutionId);
        val.setInstitutionAccountId(this.institutionAccountId);
        val.setAmount(this.amount);
        val.setValueDate(this.valueDate);
        val.setReference(this.reference);
        val.setStatus(this.status);
        val.setStatusDetail(this.statusDetail);
        val.setError(this.error);
        val.setExportedFileName(this.exportedFileName);
        val.setExportedCloudFileId(this.exportedCloudFileId);
        val.setExportedAt(this.exportedAt);
        val.setApiExport(apiExport);
        return val;
    }

    public void open(DisbursementStatusDetail statusDetail) {
        this.status = DisbursementStatus.OPEN;
        this.statusDetail = statusDetail;
    }

    public void close(DisbursementStatusDetail statusDetail) {
        this.status = DisbursementStatus.CLOSED;
        this.statusDetail = statusDetail;
    }
}
