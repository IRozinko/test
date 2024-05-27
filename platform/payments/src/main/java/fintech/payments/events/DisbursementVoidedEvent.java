package fintech.payments.events;

import fintech.payments.model.Disbursement;


public class DisbursementVoidedEvent extends AbstractDisbursementEvent {

    public DisbursementVoidedEvent(Disbursement disbursement) {
        super(disbursement);
    }
}
