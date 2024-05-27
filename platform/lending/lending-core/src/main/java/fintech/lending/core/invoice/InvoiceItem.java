package fintech.lending.core.invoice;

import fintech.lending.core.invoice.db.InvoiceItemType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class InvoiceItem {

    private InvoiceItemType type;

    private String subType;

    private BigDecimal amount = amount(0);

    private BigDecimal amountPaid = amount(0);

    private BigDecimal amountOutstanding = amount(0);

}
