package fintech.lending.core.loan.db;

import fintech.db.BaseEntity;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.InstallmentStatus;
import fintech.lending.core.loan.InstallmentStatusDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
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
@Table(name = "installment", schema = Entities.SCHEMA)
public class InstallmentEntity extends BaseEntity {

    @Column(nullable = false)
    private Long loanId;

    @Column(nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long contractId;

    @Column(nullable = true)
    private Long invoiceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallmentStatusDetail statusDetail;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate periodFrom;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate periodTo;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate valueDate;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate originalDueDate;

    @Column(columnDefinition = "DATE")
    private LocalDate generateInvoiceOnDate;

    @Column(nullable = false)
    private Long gracePeriodInDays = 0L;

    @Column(nullable = false)
    private boolean applyPenalty;

    @Column(nullable = false)
    private Long installmentSequence;

    @Column(nullable = false, unique = true)
    private String installmentNumber;

    private Long invoiceFileId;

    private String invoiceFileName;

    // ------------------------------------------------------------------
    //     Derived values from transactions
    // ------------------------------------------------------------------

    @Column(columnDefinition = "DATE")
    private LocalDate closeDate;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate dueDate;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime invoiceFileGeneratedAt;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime invoiceFileSentAt;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDue = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalInvoiced = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalInvoiced = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestApplied = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestInvoiced = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyApplied = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyInvoiced = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeApplied = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feePaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeInvoiced = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal overpaymentUsed = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cashIn = amount(0);

    @Column(nullable = false)
    private Long dpd = 0L;

    public Installment toValueObject() {
        Installment vo = new Installment();
        vo.setId(this.id);
        vo.setLoanId(this.loanId);
        vo.setScheduleId(this.scheduleId);
        vo.setClientId(this.clientId);
        vo.setContractId(this.contractId);
        vo.setInvoiceId(this.invoiceId);
        vo.setStatus(this.status);
        vo.setStatusDetail(this.statusDetail);
        vo.setPeriodFrom(this.periodFrom);
        vo.setPeriodTo(this.periodTo);
        vo.setValueDate(this.valueDate);
        vo.setDueDate(this.dueDate);
        vo.setCloseDate(this.closeDate);
        vo.setGracePeriodInDays(this.gracePeriodInDays);
        vo.setApplyPenalty(this.applyPenalty);
        vo.setInstallmentNumber(this.installmentNumber);
        vo.setGenerateInvoiceOnDate(this.generateInvoiceOnDate);
        vo.setInvoiceFileId(this.invoiceFileId);
        vo.setInvoiceFileName(this.invoiceFileName);
        vo.setInvoiceFileGeneratedAt(this.invoiceFileGeneratedAt);
        vo.setInvoiceFileSentAt(this.invoiceFileSentAt);
        vo.setInstallmentSequence(this.installmentSequence);
        vo.setTotalDue(this.totalDue);
        // for now using invoiced amount as scheduled as scheduled amount should be derived value
        vo.setTotalInvoiced(this.totalInvoiced);
        vo.setTotalScheduled(this.totalInvoiced);
        vo.setTotalPaid(this.totalPaid);
        vo.setPrincipalScheduled(this.principalInvoiced);
        vo.setPrincipalPaid(this.principalPaid);
        vo.setPrincipalWrittenOff(this.principalWrittenOff);
        vo.setPrincipalInvoiced(this.principalInvoiced);
        vo.setInterestApplied(this.interestApplied);
        vo.setInterestScheduled(this.interestInvoiced);
        vo.setInterestPaid(this.interestPaid);
        vo.setInterestWrittenOff(this.interestWrittenOff);
        vo.setInterestInvoiced(this.interestInvoiced);
        vo.setPenaltyApplied(this.penaltyApplied);
        vo.setPenaltyScheduled(this.penaltyInvoiced);
        vo.setPenaltyPaid(this.penaltyPaid);
        vo.setPenaltyWrittenOff(this.penaltyWrittenOff);
        vo.setPenaltyInvoiced(this.penaltyInvoiced);
        vo.setFeeApplied(this.feeApplied);
        vo.setFeeScheduled(this.feeInvoiced);
        vo.setFeePaid(this.feePaid);
        vo.setFeeWrittenOff(this.feeWrittenOff);
        vo.setFeeInvoiced(this.feeInvoiced);
        vo.setOverpaymentUsed(this.overpaymentUsed);
        vo.setCashIn(this.cashIn);
        vo.setDpd(this.dpd);
        return vo;
    }
}
