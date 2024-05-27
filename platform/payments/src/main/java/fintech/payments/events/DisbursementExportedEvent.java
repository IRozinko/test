package fintech.payments.events;

import fintech.payments.model.Disbursement;


public class DisbursementExportedEvent extends AbstractDisbursementEvent {

    public DisbursementExportedEvent(Disbursement disbursement) {
        super(disbursement);
    }
}
