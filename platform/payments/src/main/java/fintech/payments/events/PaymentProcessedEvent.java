package fintech.payments.events;


import fintech.payments.model.Payment;

public class PaymentProcessedEvent extends AbstractPaymentEvent {
    public PaymentProcessedEvent(Payment payment) {
        super(payment);
    }
}
