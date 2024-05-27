package fintech.lending.core.invoice.events;

import fintech.lending.core.invoice.Invoice;
import lombok.Getter;

@Getter
public class InvoiceVoidedEvent extends InvoiceEvent {

    public InvoiceVoidedEvent(Invoice invoice) {
        super(invoice);
    }
}
