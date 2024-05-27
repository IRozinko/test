package fintech.lending.core.loan.db;

import fintech.db.BaseEntity;
import fintech.lending.core.PeriodUnit;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanStatus;
import fintech.lending.core.loan.LoanStatusDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
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

import static fintech.BigDecimalUtils.amount;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@DynamicUpdate
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "loan", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_loan_client_id"),
})
public class LoanEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatusDetail statusDetail;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long loanApplicationId;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate issueDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PeriodUnit periodUnit = PeriodUnit.NA;

    @Column(nullable = false)
    private Long periodCount = 0L;

    @Column(nullable = false)
    private boolean penaltySuspended;

    @Column(nullable = false)
    private boolean compliantWithAemip;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestDiscountAmount = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestDiscountPercent = amount(0);

    private Long discountId;

    private Long interestStrategyId;

    private Long penaltyStrategyId;

    private Long extensionStrategyId;

    private Long feeStrategyId;

    private Long promoCodeId;

    // ------------------------------------------------------------------
    //     Derived values from transactions and installments
    // ------------------------------------------------------------------

    @Column(columnDefinition = "DATE")
    private LocalDate closeDate;

    @Column(columnDefinition = "DATE")
    private LocalDate maturityDate;

    @Column(columnDefinition = "DATE")
    private LocalDate paymentDueDate;

    @Column(columnDefinition = "DATE")
    private LocalDate brokenDate;

    private String reasonForBreak;

    private String portfolio;

    @Column(nullable = false)
    private String company;
    @Column(columnDefinition = "DATE")
    private LocalDate rescheduledDate;

    @Column(columnDefinition = "DATE")
    private LocalDate rescheduleBrokenDate;

    @Column(columnDefinition = "DATE")
    private LocalDate movedToLegalDate;

    @Column(columnDefinition = "DATE")
    private LocalDate firstDisbursementDate;

    @Column(nullable = false, unique = true, name = "loan_number")
    private String number;

    @Column(nullable = false)
    private Long loansPaid;

    @Column(nullable = false)
    private BigDecimal creditLimit = amount(0);

    @Column(nullable = false)
    private BigDecimal creditLimitAvailable = amount(0);

    @Column(nullable = false)
    private BigDecimal creditLimitAwarded = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalDisbursed = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalDue = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalOutstanding = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalGranted = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestApplied = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestDue = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestOutstanding = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyApplied = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyDue = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyOutstanding = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeApplied = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feePaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeDue = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeOutstanding = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDue = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalOutstanding = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cashIn = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cashOut = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal overpaymentReceived = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal overpaymentUsed = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal overpaymentRefunded = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal overpaymentAvailable = amount(0);

    private int invoicePaymentDay;

    @Column(nullable = false)
    private int overdueDays = 0;

    @Column(nullable = false)
    private int maxOverdueDays = 0;

    @Column(nullable = false)
    private Long extensions = 0L;

    @Column(nullable = false)
    private Long extendedByDays = 0L;

    public Loan toValueObject() {
        Loan value = new Loan();
        value.setId(this.id);
        value.setPortfolio(this.portfolio);
        value.setCompany(this.company);
        value.setProductId(this.productId);
        value.setClientId(this.clientId);
        value.setStatus(this.status);
        value.setStatusDetail(this.statusDetail);
        value.setNumber(this.number);
        value.setIssueDate(this.issueDate);
        value.setCloseDate(this.closeDate);
        value.setPaymentDueDate(this.paymentDueDate);
        value.setBrokenDate(this.brokenDate);
        value.setReasonForBreak(this.reasonForBreak);
        value.setRescheduledDate(this.rescheduledDate);
        value.setRescheduleBrokenDate(this.rescheduleBrokenDate);
        value.setMovedToLegalDate(this.movedToLegalDate);
        value.setFirstDisbursementDate(firstDisbursementDate);
        value.setApplicationId(this.loanApplicationId);

        value.setInterestStrategyId(this.interestStrategyId);
        value.setPenaltyStrategyId(this.penaltyStrategyId);
        value.setExtensionStrategyId(this.extensionStrategyId);
        value.setFeeStrategyId(this.feeStrategyId);
        value.setPromoCodeId(this.promoCodeId);
        value.setDiscountId(this.discountId);

        value.setLoansPaid(this.loansPaid);
        value.setCreditLimit(this.creditLimit);
        value.setCreditLimitAvailable(this.creditLimitAvailable);
        value.setCreditLimitAwarded(this.creditLimitAwarded);
        value.setMaturityDate(this.maturityDate);
        value.setPrincipalDisbursed(this.principalDisbursed);
        value.setPrincipalPaid(this.principalPaid);
        value.setPrincipalWrittenOff(this.principalWrittenOff);
        value.setPrincipalDue(this.principalDue);
        value.setPrincipalOutstanding(this.principalOutstanding);
        value.setInterestApplied(this.interestApplied);
        value.setInterestPaid(this.interestPaid);
        value.setInterestWrittenOff(this.interestWrittenOff);
        value.setInterestDue(this.interestDue);
        value.setInterestOutstanding(this.interestOutstanding);
        value.setPenaltyApplied(this.penaltyApplied);
        value.setPenaltyPaid(this.penaltyPaid);
        value.setPenaltyWrittenOff(this.penaltyWrittenOff);
        value.setPenaltyDue(this.penaltyDue);
        value.setPenaltyOutstanding(this.penaltyOutstanding);
        value.setFeeApplied(this.feeApplied);
        value.setFeePaid(this.feePaid);
        value.setFeeWrittenOff(this.feeWrittenOff);
        value.setOverpaymentReceived(this.overpaymentReceived);
        value.setOverpaymentUsed(this.overpaymentUsed);
        value.setOverpaymentRefunded(this.overpaymentRefunded);
        value.setOverpaymentAvailable(this.overpaymentAvailable);
        value.setFeeDue(this.feeDue);
        value.setFeeOutstanding(this.feeOutstanding);
        value.setTotalDue(this.totalDue);
        value.setTotalOutstanding(this.totalOutstanding);
        value.setTotalPaid(this.principalPaid.add(this.interestPaid).add(this.penaltyPaid).add(this.feePaid));
        value.setCashIn(this.cashIn);
        value.setCashOut(this.cashOut);
        value.setInvoicePaymentDay(this.invoicePaymentDay);
        value.setOverdueDays(this.overdueDays);
        value.setMaxOverdueDays(this.maxOverdueDays);
        value.setExtensions(this.extensions);
        value.setExtendedByDays(this.extendedByDays);
        value.setPeriodCount(this.periodCount);
        value.setInterestDiscountAmount(this.interestDiscountAmount);
        value.setInterestDiscountPercent(this.interestDiscountPercent);
        value.setCreatedBy(this.createdBy);
        value.setPenaltySuspended(this.penaltySuspended);
        value.setCompliantWithAEMIP(this.compliantWithAemip);
        return value;
    }

    public void close(LoanStatusDetail detail, LocalDate when) {
        status = LoanStatus.CLOSED;
        statusDetail = detail;
        closeDate = when;
    }

    public void open(LoanStatusDetail detail) {
        status = LoanStatus.OPEN;
        statusDetail = detail;
        closeDate = null;
    }

    public boolean isOpen() {
        return status == LoanStatus.OPEN;
    }

    public boolean isOpen(LoanStatusDetail statusDetail) {
        return this.status == LoanStatus.OPEN && this.statusDetail == statusDetail;
    }
}
