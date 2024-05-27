package fintech.lending.core.invoice.events;

import fintech.lending.core.invoice.Invoice;
import lombok.Getter;

@Getter
public class InvoiceEvent {

    private final Invoice invoice;

    public InvoiceEvent(Invoice invoice) {
        this.invoice = invoice;
    }

}
