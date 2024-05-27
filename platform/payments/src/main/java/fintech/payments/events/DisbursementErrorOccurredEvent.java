package fintech.payments.events;

import fintech.payments.model.Disbursement;


public class DisbursementErrorOccurredEvent extends AbstractDisbursementEvent {

    public DisbursementErrorOccurredEvent(Disbursement disbursement) {
        super(disbursement);
    }
}
