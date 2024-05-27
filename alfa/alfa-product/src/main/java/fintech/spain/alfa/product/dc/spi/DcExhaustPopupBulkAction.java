package fintech.spain.alfa.product.dc.spi;

import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.spain.alfa.product.web.model.PopupType;
import fintech.spain.alfa.product.web.spi.PopupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DcExhaustPopupBulkAction implements BulkActionHandler {

    private final PopupService popupService;

    @Autowired
    public DcExhaustPopupBulkAction(PopupService popupService) {
        this.popupService = popupService;
    }

    @Override
    public void handle(BulkActionContext context) {
        Long clientId = context.getDebt().getClientId();
        String type = context.getRequiredParam("type", String.class);

        popupService.markAsExhausted(clientId, PopupType.valueOf(type));
    }
}
