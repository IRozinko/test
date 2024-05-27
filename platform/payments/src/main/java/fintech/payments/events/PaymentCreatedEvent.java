package fintech.payments.events;


import fintech.payments.model.Payment;

public class PaymentCreatedEvent extends AbstractPaymentEvent {
    public PaymentCreatedEvent(Payment payment) {
        super(payment);
    }
}
