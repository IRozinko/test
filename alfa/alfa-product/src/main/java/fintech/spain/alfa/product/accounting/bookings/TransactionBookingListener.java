package fintech.spain.alfa.product.accounting.bookings;

import fintech.spain.alfa.product.accounting.TransactionBookingService;
import fintech.transactions.TransactionAddedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class TransactionBookingListener {

    @Autowired
    private TransactionBookingService bookingService;

    @EventListener
    public void onEvent(TransactionAddedEvent event) {
        bookingService.book(event.getTransaction());
    }
}
