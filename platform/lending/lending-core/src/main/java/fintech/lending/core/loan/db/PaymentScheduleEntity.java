package fintech.lending.core.loan.db;

import fintech.db.BaseEntity;
import fintech.lending.core.PeriodUnit;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.PaymentSchedule;
import fintech.transactions.TransactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true, of = {"startDate", "maturityDate"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "schedule", schema = Entities.SCHEMA)
public class PaymentScheduleEntity extends BaseEntity {

    @Column(nullable = false, name = "loan_id")
    private Long loanId;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private boolean latest;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate maturityDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PeriodUnit periodUnit;

    @Column(nullable = false)
    private Long periodCount;

    @Column(nullable = false)
    private Long installments;

    @Column(nullable = false)
    private Long gracePeriodInDays;

    @Enumerated(EnumType.STRING)
    private PeriodUnit extensionPeriodUnit;

    @Column(nullable = false)
    private Long extensionPeriodCount = 0L;

    private Long previousPaymentScheduleId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType sourceTransactionType;

    @Column(nullable = false)
    private Long sourceTransactionId;

    @Column(nullable = false)
    private boolean invoiceAppliedPenalty;

    @Column(nullable = false)
    private boolean invoiceAppliedInterest;

    @Column(nullable = false)
    private boolean invoiceAppliedFees;

    @Column(nullable = false)
    private boolean closeLoanOnPaid;

    @Column(nullable = false)
    private long baseOverdueDays;

    public PaymentSchedule toValueObject() {
        PaymentSchedule vo = new PaymentSchedule();
        vo.setId(this.getId());
        vo.setClientId(this.getClientId());
        vo.setLoanId(this.loanId);
        vo.setLatest(this.isLatest());
        vo.setStartDate(this.getStartDate());
        vo.setMaturityDate(this.getMaturityDate());
        vo.setPeriodUnit(this.getPeriodUnit());
        vo.setPeriodCount(this.getPeriodCount());
        vo.setInstallments(this.getInstallments());
        vo.setGracePeriodInDays(this.getGracePeriodInDays());
        vo.setExtensionPeriodCount(this.getExtensionPeriodCount());
        vo.setExtensionPeriodUnit(this.getExtensionPeriodUnit());
        vo.setInvoiceAppliedPenalty(this.invoiceAppliedPenalty);
        vo.setInvoiceAppliedInterest(this.invoiceAppliedInterest);
        vo.setInvoiceAppliedFees(this.invoiceAppliedFees);
        vo.setCloseLoanOnPaid(this.closeLoanOnPaid);
        vo.setBaseOverdueDays(this.baseOverdueDays);
        return vo;
    }
}
