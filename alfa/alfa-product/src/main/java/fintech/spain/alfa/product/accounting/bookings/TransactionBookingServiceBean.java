package fintech.spain.alfa.product.accounting.bookings;

import fintech.spain.alfa.product.accounting.TransactionBookingService;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class TransactionBookingServiceBean implements TransactionBookingService {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionBooking transactionBooking;

    @Override
    public void book(Long transactionId) {
        book(transactionService.getTransaction(transactionId));
    }

    @Override
    public void book(Transaction transaction) {
        if (transaction.getVoidsTransactionId() != null) {
            transactionBooking.bookVoid(transaction);
        } else {
            transactionBooking.book(transaction);
        }
    }

}
