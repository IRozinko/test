package fintech.lending.core.invoice.events;

import fintech.lending.core.invoice.Invoice;
import lombok.Getter;

@Getter
public class InvoiceUpdatedEvent extends InvoiceEvent {

    public InvoiceUpdatedEvent(Invoice invoice) {
        super(invoice);
    }

}
