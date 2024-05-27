package fintech.lending.core.loan.impl;

import fintech.TimeMachine;
import fintech.lending.core.loan.LoanService;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionAddedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
class LoanDerivedValueTrigger {

    @Autowired
    private LoanService loanService;

    @EventListener
    @Order
    public void onLoanTransactionAdded(TransactionAddedEvent event) {
        Transaction tx = event.getTransaction();

        if (tx.getLoanId() != null) {
            loanService.resolveLoanDerivedValues(tx.getLoanId(), TimeMachine.today());
        }
    }

}
