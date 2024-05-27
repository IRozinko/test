package fintech.payments.events;

import fintech.payments.model.Disbursement;


public class DisbursementExportErrorOccurredEvent extends AbstractDisbursementEvent {

    public DisbursementExportErrorOccurredEvent(Disbursement disbursement) {
        super(disbursement);
    }
}
