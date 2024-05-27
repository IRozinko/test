package fintech.payments.events;


import fintech.payments.model.Payment;

public class PaymentVoidedEvent extends AbstractPaymentEvent {
    public PaymentVoidedEvent(Payment payment) {
        super(payment);
    }
}
