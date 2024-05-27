package fintech.spain.alfa.product.dc.spi;

import fintech.dc.model.Debt;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.spain.alfa.product.cms.AlfaCmsContextBuilder;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DcSendSmsBulkAction implements BulkActionHandler {

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    @Autowired
    private AlfaCmsContextBuilder contextBuilder;

    @Override
    public void handle(BulkActionContext context) {
        String text = context.getRequiredParam("text", String.class);
        String cmsKey = context.getRequiredParam("cmsKey", String.class);

        Debt debt = context.getDebt();

        notificationFactory.fromLoan(debt.getLoanId())
            .cmsKey(cmsKey)
            .smsText(text)
            .loanId(debt.getLoanId())
            .debtId(debt.getId())
            .render(contextBuilder.basicContext(debt.getClientId(), debt.getId()), false)
            .send();
    }
}
