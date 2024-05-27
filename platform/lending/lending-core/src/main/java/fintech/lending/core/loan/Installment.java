package fintech.lending.core.loan;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class Installment {

    private Long id;
    private Long loanId;
    private Long scheduleId;
    private Long clientId;
    private Long contractId;
    private Long invoiceId;
    private InstallmentStatus status;
    private InstallmentStatusDetail statusDetail;
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private LocalDate valueDate;
    private LocalDate dueDate;
    private LocalDate closeDate;
    private Long gracePeriodInDays = 0L;
    private Long installmentSequence;
    private String installmentNumber;
    private LocalDate generateInvoiceOnDate;
    private Long invoiceFileId;
    private String invoiceFileName;
    private LocalDateTime invoiceFileGeneratedAt;
    private LocalDateTime invoiceFileSentAt;
    private boolean applyPenalty;
    private BigDecimal totalDue = amount(0);
    private BigDecimal totalInvoiced = amount(0);
    private BigDecimal totalScheduled = amount(0);
    private BigDecimal totalPaid = amount(0);
    private BigDecimal principalScheduled = amount(0);
    private BigDecimal principalPaid = amount(0);
    private BigDecimal principalWrittenOff = amount(0);
    private BigDecimal principalInvoiced = amount(0);
    private BigDecimal interestApplied = amount(0);
    private BigDecimal interestScheduled = amount(0);
    private BigDecimal interestPaid = amount(0);
    private BigDecimal interestWrittenOff = amount(0);
    private BigDecimal interestInvoiced = amount(0);
    private BigDecimal penaltyApplied = amount(0);
    private BigDecimal penaltyScheduled = amount(0);
    private BigDecimal penaltyPaid = amount(0);
    private BigDecimal penaltyWrittenOff = amount(0);
    private BigDecimal penaltyInvoiced = amount(0);
    private BigDecimal feeApplied = amount(0);
    private BigDecimal feeScheduled = amount(0);
    private BigDecimal feePaid = amount(0);
    private BigDecimal feeWrittenOff = amount(0);
    private BigDecimal feeInvoiced = amount(0);
    private BigDecimal overpaymentUsed = amount(0);
    private BigDecimal cashIn = amount(0);
    private Long dpd = 0L;
}
