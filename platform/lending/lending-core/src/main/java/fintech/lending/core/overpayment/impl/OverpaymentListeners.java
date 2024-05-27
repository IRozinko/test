package fintech.lending.core.overpayment.impl;

import fintech.Validate;
import fintech.transactions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static fintech.BigDecimalUtils.isZero;

@Component
public class OverpaymentListeners {

    @Autowired
    TransactionService transactionService;

    @EventListener
    public void validateOverpaymentBalance(TransactionAddedEvent event) {
        Transaction tx = event.getTransaction();
        if (isZero(tx.getOverpaymentReceived()) &&
            isZero(tx.getOverpaymentUsed()) &&
            isZero(tx.getOverpaymentRefunded())) {
            // no overpayment amounts, skip validation
            return;
        }
        Validate.notNull(tx.getClientId(), "Client id required for transaction with overpayment amounts");
        Balance balance = transactionService.getBalance(TransactionQuery.byClient(tx.getClientId()));
        Validate.isZeroOrPositive(balance.getOverpaymentReceived(), "Overpayment received must be >= 0: [%s]", balance.getOverpaymentReceived());
        Validate.isZeroOrPositive(balance.getOverpaymentUsed(), "Overpayment used must be >= 0: [%s]", balance.getOverpaymentUsed());
        Validate.isZeroOrPositive(balance.getOverpaymentRefunded(), "Overpayment refunded must be >= 0: [%s]", balance.getOverpaymentRefunded());
        Validate.isZeroOrPositive(balance.getOverpaymentAvailable(), "Overpayment available must be >= 0: [%s]", balance.getOverpaymentAvailable());
    }
}
