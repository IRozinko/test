package fintech.spain.alfa.product.dc.spi;

import fintech.dc.model.Debt;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DcSendNotificationBulkAction implements BulkActionHandler {

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    @Autowired
    private AlfaCmsModels cmsModels;

    @Override
    public void handle(BulkActionContext context) {
        String cmsKey = context.getRequiredParam("cmsKey", String.class);
        Debt debt = context.getDebt();
        Map<String, Object> cmsContext = cmsModels.debtContext(debt.getId());
        notificationFactory.fromLoan(debt.getLoanId())
            .loanId(debt.getLoanId())
            .debtId(debt.getId())
            .render(cmsKey, cmsContext)
            .send();
    }
}
