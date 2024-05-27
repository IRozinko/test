package fintech.lending.core.loan.commands;

import fintech.transactions.AddTransactionCommand;
import fintech.transactions.TransactionType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class AddInstallmentCommand {

    // to preserve compatibility with old invoice model
    private TransactionType transactionType = TransactionType.INSTALLMENT;
    private Long invoiceId;

    private Long scheduleId;
    private Long contractId;

    private LocalDate periodFrom;
    private LocalDate periodTo;
    private LocalDate valueDate;
    private LocalDate dueDate;
    private LocalDate generateInvoiceOnDate;
    private Long gracePeriodInDays = 0L;
    private boolean applyPenalty;
    private Long installmentSequence;
    private String installmentNumber;


    private BigDecimal principalInvoiced = amount(0);
    private BigDecimal principalWrittenOff = amount(0);
    private BigDecimal interestApplied = amount(0);
    private BigDecimal interestInvoiced = amount(0);
    private BigDecimal interestWrittenOff = amount(0);
    private BigDecimal penaltyApplied = amount(0);
    private BigDecimal penaltyInvoiced = amount(0);
    private BigDecimal penaltyWrittenOff = amount(0);

    private List<AddTransactionCommand.TransactionEntry> entries = newArrayList();

    public void addEntry(AddTransactionCommand.TransactionEntry entry) {
        entries.add(entry);
    }
}
