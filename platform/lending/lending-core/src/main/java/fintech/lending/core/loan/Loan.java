package fintech.lending.core.loan;

import fintech.TimeMachine;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Data
public class Loan {

    private Long id;
    private Long productId;
    private Long clientId;
    private Long applicationId;

    private Long interestStrategyId;
    private Long penaltyStrategyId;
    private Long extensionStrategyId;
    private Long feeStrategyId;
    private Long promoCodeId;
    private Long discountId;

    private LoanStatus status;
    private LoanStatusDetail statusDetail;
    private String number;
    private Long loansPaid;
    private LocalDate issueDate;
    private LocalDate closeDate;
    private LocalDate maturityDate;
    private LocalDate paymentDueDate;
    private LocalDate brokenDate;
    private String reasonForBreak;
    private LocalDate rescheduledDate;
    private LocalDate rescheduleBrokenDate;
    private LocalDate movedToLegalDate;
    private LocalDate firstDisbursementDate;
    private Long periodCount;
    private BigDecimal interestDiscountAmount = amount(0);
    private BigDecimal interestDiscountPercent = amount(0);
    private BigDecimal creditLimit = amount(0);
    private BigDecimal creditLimitAvailable = amount(0);
    private BigDecimal creditLimitAwarded = amount(0);
    private BigDecimal principalDisbursed = amount(0);
    private BigDecimal principalPaid = amount(0);
    private BigDecimal principalWrittenOff = amount(0);
    private BigDecimal principalDue = amount(0);
    private BigDecimal principalOutstanding = amount(0);
    private BigDecimal interestApplied = amount(0);
    private BigDecimal interestPaid = amount(0);
    private BigDecimal interestWrittenOff = amount(0);
    private BigDecimal interestDue = amount(0);
    private BigDecimal interestOutstanding = amount(0);
    private BigDecimal penaltyApplied = amount(0);
    private BigDecimal penaltyPaid = amount(0);
    private BigDecimal penaltyWrittenOff = amount(0);
    private BigDecimal penaltyDue = amount(0);
    private BigDecimal penaltyOutstanding = amount(0);
    private BigDecimal feeApplied = amount(0);
    private BigDecimal feePaid = amount(0);
    private BigDecimal feeWrittenOff = amount(0);
    private BigDecimal feeDue = amount(0);
    private BigDecimal feeOutstanding = amount(0);
    private BigDecimal totalPaid = amount(0);
    private BigDecimal totalDue = amount(0);
    private BigDecimal totalOutstanding = amount(0);
    private BigDecimal overpaymentReceived = amount(0);
    private BigDecimal overpaymentUsed = amount(0);
    private BigDecimal overpaymentRefunded = amount(0);
    private BigDecimal overpaymentAvailable = amount(0);
    private BigDecimal cashIn = amount(0);
    private BigDecimal cashOut = amount(0);
    private int invoicePaymentDay;
    private int overdueDays = 0;
    private int maxOverdueDays = 0;
    private Long extensions = 0L;
    private Long extendedByDays = 0L;
    private String createdBy;
    private boolean penaltySuspended;
    private boolean compliantWithAEMIP;
    private String portfolio;
    private String company;

    public boolean isLoanBeforeEndOfTerm() {
        return isOpen() && TimeMachine.today().isBefore(issueDate.plusDays(periodCount));
    }

    public boolean isDebt() {
        return isOpen() && TimeMachine.today().isAfter(maturityDate);
    }


    public boolean isOpen(LoanStatusDetail statusDetail) {
        return isOpen() && this.statusDetail == statusDetail;
    }

    public boolean isOpen() {
        return this.status == LoanStatus.OPEN;
    }

    public boolean isClosed() {
        return this.status == LoanStatus.CLOSED;
    }

}
