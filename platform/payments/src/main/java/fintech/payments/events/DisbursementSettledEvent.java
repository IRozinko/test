package fintech.payments.events;

import fintech.payments.model.Disbursement;


public class DisbursementSettledEvent extends AbstractDisbursementEvent {

    public DisbursementSettledEvent(Disbursement disbursement) {
        super(disbursement);
    }
}
