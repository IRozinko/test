package fintech.transactions;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.amount;
import static fintech.TimeMachine.today;
import static fintech.transactions.TransactionType.SOLD_LOAN;

@Data
@Accessors(chain = true)
public class AddTransactionCommand {

    private TransactionType transactionType;
    private String transactionSubType;
    private LocalDate postDate;
    private LocalDate valueDate;
    private LocalDate bookingDate;

    private Long paymentId;
    private Long institutionId;
    private Long institutionAccountId;
    private Long clientId;
    private Long loanId;
    private Long applicationId;
    private Long productId;
    private Long disbursementId;
    private Long invoiceId;
    private Long scheduleId;
    private Long contractId;
    private Long installmentId;

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
    private BigDecimal overpaymentReceived = amount(0);
    private BigDecimal overpaymentUsed = amount(0);
    private BigDecimal overpaymentRefunded = amount(0);
    private BigDecimal creditLimit = amount(0);
    private BigDecimal creditLimitAvailable = amount(0);
    private BigDecimal cashIn = amount(0);
    private BigDecimal cashOut = amount(0);

    private String comments;

    private Integer dpd;

    private Long extensionDays = 0L;
    private Long extension = 0L;

    private List<TransactionEntry> entries = newArrayList();

    @Data
    @Accessors(chain = true)
    public static class TransactionEntry {

        private TransactionEntryType type;

        private String subType;

        private BigDecimal amountApplied = amount(0);

        private BigDecimal amountPaid = amount(0);

        private BigDecimal amountInvoiced = amount(0);

        private BigDecimal amountWrittenOff = amount(0);
    }

    public void addEntry(TransactionEntry entry) {
        entries.add(entry);
    }

    public static AddTransactionCommand soldLoan(Balance balance) {
       return new AddTransactionCommand()
            .setTransactionType(SOLD_LOAN)
            .setPrincipalWrittenOff(balance.getPrincipalDue())
            .setPrincipalInvoiced(balance.getPrincipalDue().negate())
            .setInterestWrittenOff(balance.getInterestDue())
            .setInterestInvoiced(balance.getInterestDue().negate())
            .setPenaltyWrittenOff(balance.getPenaltyDue())
            .setPenaltyInvoiced(balance.getPenaltyDue().negate())
            .setValueDate(today());
    }
}
