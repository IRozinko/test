package fintech.spain.alfa.product.activity;

import fintech.activity.model.Activity;
import fintech.activity.spi.BulkActionContext;
import fintech.activity.spi.BulkActionHandler;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivitySendSmsBulkAction implements BulkActionHandler {

    @Autowired
    private AlfaNotificationBuilderFactory alfaNotificationBuilderFactory;

    @Override
    public void handle(BulkActionContext context) {
        String text = context.getRequiredParam("text", String.class);
        String cmsKey = context.getRequiredParam("cmsKey", String.class);

        Activity activity = context.getActivity();
        alfaNotificationBuilderFactory.fromCustomerService(activity.getClientId())
            .cmsKey(cmsKey)
            .smsText(text)
            .loanApplicationId(activity.getApplicationId())
            .loanId(activity.getLoanId())
            .taskId(activity.getTaskId())
            .debtId(activity.getDebtId())
            .send();
    }
}
