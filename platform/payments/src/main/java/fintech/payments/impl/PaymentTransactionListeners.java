package fintech.payments.impl;


import com.google.common.collect.ImmutableSet;
import fintech.BigDecimalUtils;
import fintech.Validate;
import fintech.payments.db.PaymentEntity;
import fintech.payments.db.PaymentRepository;
import fintech.payments.events.PaymentPendingEvent;
import fintech.payments.events.PaymentProcessedEvent;
import fintech.payments.model.PaymentStatusDetail;
import fintech.payments.model.PaymentType;
import fintech.transactions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.google.common.collect.ImmutableSet.of;

@Slf4j
@Component
public class PaymentTransactionListeners {

    private static final ImmutableSet<TransactionType> TRANSACTION_TYPES_WITHOUT_MANDATORY_DISTRIBUTION = of(
        TransactionType.PAYMENT,
        TransactionType.VOID_PAYMENT,
        TransactionType.MANUAL,
        TransactionType.VOID_MANUAL,
        TransactionType.DISBURSEMENT_SETTLEMENT,
        TransactionType.VOID_DISBURSEMENT_SETTLEMENT);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener(TransactionAddedEvent.class)
    public void transactionAdded(TransactionAddedEvent event) {
        update(event.getTransaction());
    }

    private void update(Transaction tx) {
        Long paymentId = tx.getPaymentId();
        if (paymentId == null) {
            Validate.isZero(tx.getCashIn(), "Cash in not allowed without payment id, [%s]", tx);
            Validate.isZero(tx.getCashOut(), "Cash out not allowed without payment id, [%s]", tx);
            return;
        }
        PaymentEntity payment = repository.getRequired(paymentId);

        validate(tx, payment);

        BigDecimal processedAmount = processedAmount(payment);
        Validate.isLoe(processedAmount, payment.getAmount(), "Payment processed amount [%s] more than total: [%s]", processedAmount, payment);
        payment.setPendingAmount(payment.getAmount().subtract(processedAmount));
        log.info("Updated payment pending amount: [{}]", payment);
        if (BigDecimalUtils.isZero(payment.getPendingAmount())) {
            log.info("Payment processed: [{}]", payment);
            payment.close(PaymentStatusDetail.PROCESSED);
            eventPublisher.publishEvent(new PaymentProcessedEvent(payment.toValueObject()));
        } else {
            payment.open(PaymentStatusDetail.MANUAL);
            eventPublisher.publishEvent(new PaymentPendingEvent(payment.toValueObject()));
        }
    }

    private void validate(Transaction tx, PaymentEntity payment) {
        if (payment.getPaymentType() == PaymentType.INCOMING) {
            Validate.isZero(tx.getCashOut(), "Cash out not allowed for incoming payment");
            Validate.isNotZero(tx.getCashIn(), "Cash in amount required for incoming payment");
            if (!TRANSACTION_TYPES_WITHOUT_MANDATORY_DISTRIBUTION.contains(tx.getTransactionType())) {
                BigDecimal distributedAmount = tx.getFeePaid().add(tx.getInterestPaid()).add(tx.getPrincipalPaid()).add(tx.getPenaltyPaid()).add(tx.getOverpaymentReceived().add(tx.getEarlyRepaymentReceived()));
                Validate.isEqual(distributedAmount, tx.getCashIn().add(tx.getOverpaymentUsed()), "Cash in and distributed amount does not match: [%s]", tx);
            }
        } else {
            Validate.isZero(tx.getCashIn(), "Cash in not allowed for outgoing payment");
            Validate.isNotZero(tx.getCashOut(), "Cash out amount required for outgoing payment");
            if (!TRANSACTION_TYPES_WITHOUT_MANDATORY_DISTRIBUTION.contains(tx.getTransactionType())) {
                BigDecimal distributedAmount = tx.getPrincipalDisbursed().add(tx.getOverpaymentRefunded());
                Validate.isEqual(distributedAmount, tx.getCashOut(), "Cash out and distributed amount does not match: [%s]", tx);
            }
        }

        if (TRANSACTION_TYPES_WITHOUT_MANDATORY_DISTRIBUTION.contains(tx.getTransactionType())) {
            Validate.isZero(tx.getPrincipalDisbursed(), "Principal disbursed not allowed, [%s]", tx);
            Validate.isZero(tx.getPrincipalPaid(), "Principal paid not allowed, [%s]", tx);
            Validate.isZero(tx.getFeePaid(), "Fee paid not allowed, [%s]", tx);
            Validate.isZero(tx.getInterestPaid(), "Interest paid not allowed, [%s]", tx);
            Validate.isZero(tx.getPenaltyPaid(), "Penalty paid not allowed, [%s]", tx);
            Validate.isZero(tx.getOverpaymentReceived(), "Overpayment received not allowed, [%s]", tx);
            Validate.isZero(tx.getOverpaymentRefunded(), "Overpayment refunded not allowed, [%s]", tx);
            Validate.isZero(tx.getEarlyRepaymentReceived(), "Early repayment received not allowed, [%s]", tx);
        }
    }

    private BigDecimal processedAmount(PaymentEntity payment) {
        Balance balance = transactionService.getBalance(BalanceQuery.byPayment(payment.getId()));
        if (payment.getPaymentType() == PaymentType.INCOMING) {
            return balance.getCashIn();
        } else {
            return balance.getCashOut();
        }
    }
}
