package fintech.transactions;

import lombok.Data;

import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Data
public class EntryBalance {

    private TransactionEntryType type;
    private String subType;
    private BigDecimal amountApplied = amount(0);
    private BigDecimal amountPaid = amount(0);
    private BigDecimal amountWrittenOff = amount(0);
    private BigDecimal amountInvoiced = amount(0);
    private BigDecimal amountDue = amount(0);
    private BigDecimal amountOutstanding = amount(0);

}
