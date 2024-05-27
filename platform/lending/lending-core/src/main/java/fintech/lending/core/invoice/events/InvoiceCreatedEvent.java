package fintech.lending.core.invoice.events;

import fintech.lending.core.invoice.Invoice;
import lombok.Getter;

@Getter
public class InvoiceCreatedEvent extends InvoiceEvent {

    public InvoiceCreatedEvent(Invoice invoice) {
        super(invoice);
    }
}
