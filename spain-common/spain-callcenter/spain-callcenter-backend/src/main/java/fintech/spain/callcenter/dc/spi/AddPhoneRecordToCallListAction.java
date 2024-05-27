package fintech.spain.callcenter.dc.spi;

import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import fintech.spain.callcenter.CallCenterException;
import fintech.spain.callcenter.CallCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AddPhoneRecordToCallListAction implements ActionHandler {

    private final CallCenterService callCenterService;

    public AddPhoneRecordToCallListAction(CallCenterService callCenterService) {
        this.callCenterService = callCenterService;
    }

    @Override
    public void handle(ActionContext context) {
        Long clientId = context.getDebt().getClientId();

        try {
            callCenterService.addPhoneRecordsToCallList(clientId);
        } catch (CallCenterException e) {
            log.error("Error adding call to list for debt [{}]", context.getDebt());
        }
    }
}
