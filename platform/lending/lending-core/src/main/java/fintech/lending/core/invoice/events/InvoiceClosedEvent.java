package fintech.lending.core.invoice.events;

import fintech.lending.core.invoice.Invoice;
import lombok.Getter;

@Getter
public class InvoiceClosedEvent extends InvoiceEvent {

    public InvoiceClosedEvent(Invoice invoice) {
        super(invoice);
    }
}
