package fintech.payments.events;


import fintech.payments.model.Payment;

public class AbstractPaymentEvent {

    private final Payment payment;

    public AbstractPaymentEvent(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }
}
