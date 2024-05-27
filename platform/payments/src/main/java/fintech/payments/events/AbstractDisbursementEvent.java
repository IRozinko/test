package fintech.payments.events;

import fintech.payments.model.Disbursement;


public abstract class AbstractDisbursementEvent {

    private final Disbursement disbursement;

    public AbstractDisbursementEvent(Disbursement disbursement) {
        this.disbursement = disbursement;
    }

    public Disbursement getDisbursement() {
        return disbursement;
    }
}
