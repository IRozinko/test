package fintech.spain.callcenter.dc.spi;

import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.spain.callcenter.CallCenterException;
import fintech.spain.callcenter.CallCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemovePhoneRecordsFromCallListBulkAction implements BulkActionHandler {

    private final CallCenterService callCenterService;

    public RemovePhoneRecordsFromCallListBulkAction(CallCenterService callCenterService) {
        this.callCenterService = callCenterService;
    }

    @Override
    public void handle(BulkActionContext context) {
        Long clientId = context.getDebt().getClientId();

        try {
            callCenterService.removePhoneRecordsFromCallList(clientId);
        } catch (CallCenterException e) {
            log.error("Error removing call from list for debt [{}]", context.getDebt());
        }
    }
}
