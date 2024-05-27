package fintech.transactions;

import lombok.Data;

import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Data
public class Balance {

    private BigDecimal totalOutstanding = amount(0);
    private BigDecimal totalPaid = amount(0);
    private BigDecimal totalInvoiced = amount(0);
    private BigDecimal totalDue = amount(0);

    private BigDecimal principalDisbursed = amount(0);
    private BigDecimal principalPaid = amount(0);
    private BigDecimal principalWrittenOff = amount(0);
    private BigDecimal principalOutstanding = amount(0);
    private BigDecimal principalDue = amount(0);
    private BigDecimal principalInvoiced = amount(0);

    private BigDecimal interestApplied = amount(0);
    private BigDecimal interestPaid = amount(0);
    private BigDecimal interestWrittenOff = amount(0);
    private BigDecimal interestOutstanding = amount(0);
    private BigDecimal interestDue = amount(0);
    private BigDecimal interestInvoiced = amount(0);

    private BigDecimal feeApplied = amount(0);
    private BigDecimal feePaid = amount(0);
    private BigDecimal feeWrittenOff = amount(0);
    private BigDecimal feeOutstanding = amount(0);
    private BigDecimal feeDue = amount(0);
    private BigDecimal feeInvoiced = amount(0);

    private BigDecimal penaltyApplied = amount(0);
    private BigDecimal penaltyPaid = amount(0);
    private BigDecimal penaltyWrittenOff = amount(0);
    private BigDecimal penaltyOutstanding = amount(0);
    private BigDecimal penaltyDue = amount(0);
    private BigDecimal penaltyInvoiced = amount(0);

    private BigDecimal cashIn = amount(0);
    private BigDecimal cashOut = amount(0);

    private BigDecimal overpaymentReceived = amount(0);
    private BigDecimal overpaymentUsed = amount(0);
    private BigDecimal overpaymentRefunded = amount(0);
    private BigDecimal overpaymentAvailable = amount(0);
    
    private BigDecimal unsettledDisbursement = amount(0);

    private Long extensions = 0L;
    private Long extensionDays = 0L;

    private BigDecimal creditLimit = amount(0);
    private BigDecimal creditLimitAvailable = amount(0);

}
