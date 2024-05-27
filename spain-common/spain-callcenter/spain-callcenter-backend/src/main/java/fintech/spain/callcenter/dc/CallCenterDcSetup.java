package fintech.spain.callcenter.dc;

import fintech.dc.spi.DcRegistry;
import fintech.spain.callcenter.dc.spi.AddPhoneRecordToCallListAction;
import fintech.spain.callcenter.dc.spi.AddPhoneRecordToCallListBulkAction;
import fintech.spain.callcenter.dc.spi.RemovePhoneRecordsFromCallListAction;
import fintech.spain.callcenter.dc.spi.RemovePhoneRecordsFromCallListBulkAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallCenterDcSetup {

    private final DcRegistry dcRegistry;

    @Autowired
    public CallCenterDcSetup(DcRegistry dcRegistry) {
        this.dcRegistry = dcRegistry;
    }

    public void init() {
        dcRegistry.registerActionHandler("AddPhoneRecordToCallList", AddPhoneRecordToCallListAction.class);
        dcRegistry.registerActionHandler("RemovePhoneRecordsFromCallList", RemovePhoneRecordsFromCallListAction.class);
        dcRegistry.registerBulkActionHandler("AddPhoneRecordToCallList", AddPhoneRecordToCallListBulkAction.class);
        dcRegistry.registerBulkActionHandler("RemovePhoneRecordsFromCallList", RemovePhoneRecordsFromCallListBulkAction.class);
    }
}
