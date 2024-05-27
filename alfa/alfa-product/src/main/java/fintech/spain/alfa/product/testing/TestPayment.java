package fintech.spain.alfa.product.testing;

import fintech.payments.PaymentService;
import fintech.payments.model.Payment;
import fintech.payments.model.PaymentStatusDetail;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionQuery;
import fintech.transactions.TransactionService;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Accessors(chain = true)
public class TestPayment {

    @Getter
    private final Long paymentId;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TransactionService transactionService;

    public TestPayment(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Payment getPayment() {
        return paymentService.getPayment(paymentId);
    }

    public boolean isPending() {
        return getPayment().getStatusDetail() == PaymentStatusDetail.PENDING;
    }

    public boolean isManual() {
        return getPayment().getStatusDetail() == PaymentStatusDetail.MANUAL;
    }

    public boolean isProcessed() {
        return getPayment().getStatusDetail() == PaymentStatusDetail.PROCESSED;
    }

    public List<Transaction> getTransactions() {
        return transactionService.findTransactions(TransactionQuery.byPayment(paymentId));
    }
}
