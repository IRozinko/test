package fintech.spain.callcenter.dc.spi;

import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import fintech.spain.callcenter.CallCenterException;
import fintech.spain.callcenter.CallCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemovePhoneRecordsFromCallListAction implements ActionHandler {

    private final CallCenterService callCenterService;

    public RemovePhoneRecordsFromCallListAction(CallCenterService callCenterService) {
        this.callCenterService = callCenterService;
    }

    @Override
    public void handle(ActionContext context) {
        Long clientId = context.getDebt().getClientId();

        try {
            callCenterService.removePhoneRecordsFromCallList(clientId);
        } catch (CallCenterException e) {
            log.error("Error removing call from list for debt [{}]", context.getDebt());
        }
    }
}
