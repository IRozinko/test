package fintech.transactions;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.amount;

@Data
public class Transaction {

    private Long id;

    private TransactionType transactionType;
    private String transactionSubType;
    private LocalDate postDate;
    private LocalDate bookingDate;
    private LocalDate valueDate;

    private Long clientId;
    private Long paymentId;
    private Long loanId;
    private Long institutionId;
    private Long institutionAccountId;
    private Long productId;
    private Long disbursementId;
    private Long invoiceId;
    private Long scheduleId;
    private Long installmentId;
    private Long contractId;

    private BigDecimal principalDisbursed = amount(0);
    private BigDecimal principalPaid = amount(0);
    private BigDecimal principalWrittenOff = amount(0);
    private BigDecimal principalInvoiced = amount(0);
    private BigDecimal interestApplied = amount(0);
    private BigDecimal interestPaid = amount(0);
    private BigDecimal interestWrittenOff = amount(0);
    private BigDecimal interestInvoiced = amount(0);
    private BigDecimal penaltyApplied = amount(0);
    private BigDecimal penaltyPaid = amount(0);
    private BigDecimal penaltyWrittenOff = amount(0);
    private BigDecimal penaltyInvoiced = amount(0);
    private BigDecimal feeApplied = amount(0);
    private BigDecimal feePaid = amount(0);
    private BigDecimal feeWrittenOff = amount(0);
    private BigDecimal feeInvoiced = amount(0);
    private BigDecimal cashIn = amount(0);
    private BigDecimal cashOut = amount(0);
    private BigDecimal earlyRepaymentReceived = amount(0);
    private BigDecimal overpaymentReceived = amount(0);
    private BigDecimal overpaymentUsed = amount(0);
    private BigDecimal overpaymentRefunded = amount(0);
    private BigDecimal creditLimit = amount(0);
    private BigDecimal creditLimitAvailable = amount(0);

    private String comments;

    private Long extension = 0L;
    private Long extensionDays = 0L;

    private Integer dpd;

    private Long voidsTransactionId;
    private boolean voided;
    private LocalDate voidedDate;

    private LocalDateTime createdAt;
    private String createdBy;

    private List<TransactionEntry> entries = newArrayList();
}
