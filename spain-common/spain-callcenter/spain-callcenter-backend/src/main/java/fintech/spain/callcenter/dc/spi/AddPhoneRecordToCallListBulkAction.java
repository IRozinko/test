package fintech.spain.callcenter.dc.spi;

import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.spain.callcenter.CallCenterException;
import fintech.spain.callcenter.CallCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AddPhoneRecordToCallListBulkAction implements BulkActionHandler {

    private final CallCenterService callCenterService;

    public AddPhoneRecordToCallListBulkAction(CallCenterService callCenterService) {
        this.callCenterService = callCenterService;
    }

    @Override
    public void handle(BulkActionContext context) {
        Long clientId = context.getDebt().getClientId();

        try {
            callCenterService.addPhoneRecordsToCallList(clientId);
        } catch (CallCenterException e) {
            log.error("Error adding call to list for debt [{}]", context.getDebt());
        }
    }
}
