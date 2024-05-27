package fintech.lending.core.invoice.spi;

import fintech.lending.core.invoice.commands.GenerateInvoiceCommand;
import fintech.lending.core.invoice.commands.GeneratedInvoice;
import lombok.NonNull;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

public interface InvoicingStrategy {

    Long generateInvoice(@Valid GenerateInvoiceCommand command);

    List<GeneratedInvoice.GeneratedInvoiceItem> calculate(@NonNull Long loanId, @NonNull LocalDate periodFrom, @NonNull LocalDate periodTo);

}
