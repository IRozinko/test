package fintech.payments.events;

import fintech.payments.model.Disbursement;


public class DisbursementPendingEvent extends AbstractDisbursementEvent {

    public DisbursementPendingEvent(Disbursement disbursement) {
        super(disbursement);
    }
}
