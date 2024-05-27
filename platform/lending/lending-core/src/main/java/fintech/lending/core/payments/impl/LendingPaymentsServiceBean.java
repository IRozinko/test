package fintech.lending.core.payments.impl;

import fintech.Validate;
import fintech.lending.core.payments.AddPaymentTransactionCommand;
import fintech.lending.core.payments.LendingPaymentsService;
import fintech.lending.core.util.TransactionBuilder;
import fintech.payments.PaymentService;
import fintech.payments.model.Payment;
import fintech.payments.model.PaymentType;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class LendingPaymentsServiceBean implements LendingPaymentsService {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionBuilder transactionBuilder;

    @Autowired
    private PaymentService paymentService;

    @Override
    public Long addPaymentTransaction(AddPaymentTransactionCommand command) {
        Validate.notNull(command.getTransactionSubType(), "Transaction sub type required");
        Payment payment = paymentService.getPayment(command.getPaymentId());
        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.PAYMENT);
        tx.setComments(command.getComments());
        tx.setTransactionSubType(command.getTransactionSubType());
        tx.setClientId(command.getClientId());
        if (payment.getPaymentType() == PaymentType.INCOMING) {
            tx.setCashIn(command.getAmount());
        } else {
            tx.setCashOut(command.getAmount());
        }
        transactionBuilder.addPaymentValues(command.getPaymentId(), tx);
        return transactionService.addTransaction(tx);
    }
}
