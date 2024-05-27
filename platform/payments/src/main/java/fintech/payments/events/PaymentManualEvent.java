package fintech.payments.events;


import fintech.payments.model.Payment;

public class PaymentManualEvent extends AbstractPaymentEvent {
    public PaymentManualEvent(Payment payment) {
        super(payment);
    }
}
