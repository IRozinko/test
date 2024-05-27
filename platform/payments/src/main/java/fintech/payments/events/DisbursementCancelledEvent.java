package fintech.payments.events;

import fintech.payments.model.Disbursement;


public class DisbursementCancelledEvent extends AbstractDisbursementEvent {

    public DisbursementCancelledEvent(Disbursement disbursement) {
        super(disbursement);
    }
}
