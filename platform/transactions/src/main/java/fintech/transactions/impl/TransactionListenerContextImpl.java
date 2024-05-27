package fintech.transactions.impl;

import fintech.transactions.db.TransactionRepository;

public class TransactionListenerContextImpl {

    private final Long transactionId;
    private final TransactionRepository transactionRepository;

    public TransactionListenerContextImpl(Long transactionId, TransactionRepository transactionRepository) {
        this.transactionId = transactionId;
        this.transactionRepository = transactionRepository;
    }

}
