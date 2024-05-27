package fintech.transactions;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class TransactionEntry {

    private TransactionEntryType type;

    private String subType;

    private BigDecimal amountApplied = amount(0);

    private BigDecimal amountPaid = amount(0);

    private BigDecimal amountWrittenOff = amount(0);

    private BigDecimal amountInvoiced = amount(0);

    private BigDecimal amountScheduled = amount(0);

}
