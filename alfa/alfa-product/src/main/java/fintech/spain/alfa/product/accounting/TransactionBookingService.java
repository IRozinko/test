package fintech.spain.alfa.product.accounting;

import fintech.transactions.Transaction;

public interface TransactionBookingService {

    void book(Long transactionId);

    void book(Transaction transaction);

}
