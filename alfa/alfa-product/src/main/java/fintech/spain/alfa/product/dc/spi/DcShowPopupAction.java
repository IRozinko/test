package fintech.spain.alfa.product.dc.spi;

import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import fintech.spain.alfa.product.web.spi.PopupService;
import fintech.spain.alfa.product.web.model.PopupType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DcShowPopupAction implements ActionHandler {

    private final PopupService popupService;

    @Autowired
    public DcShowPopupAction(PopupService popupService) {
        this.popupService = popupService;
    }

    @Override
    public void handle(ActionContext context) {
        Long clientId = context.getDebt().getClientId();
        String type = context.getRequiredParam("type", String.class);

        popupService.show(clientId, PopupType.valueOf(type));
    }
}
