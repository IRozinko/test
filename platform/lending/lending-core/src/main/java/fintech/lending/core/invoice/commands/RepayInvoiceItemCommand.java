package fintech.lending.core.invoice.commands;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Data
public class RepayInvoiceItemCommand {

    @NotNull
    private Long invoiceId;

    @NotNull
    private Long invoiceItemId;

    @NotNull
    private BigDecimal amountPaid = amount(0);
}
