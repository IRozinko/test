package fintech.payments.events;


import fintech.payments.model.Payment;

public class PaymentPendingEvent extends AbstractPaymentEvent {
    public PaymentPendingEvent(Payment payment) {
        super(payment);
    }
}
