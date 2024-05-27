package fintech.spain.alfa.product.dc.spi;

import com.google.common.collect.ImmutableMap;
import fintech.dc.model.Debt;
import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import fintech.spain.notification.NotificationBuilder;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
public class DcSendNotificationAction implements ActionHandler {

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    @Autowired
    private AlfaCmsModels cmsModels;

    private final Map<String, Function<Debt, NotificationBuilder>> NOTIFICATION_PROVIDERS = ImmutableMap.<String, Function<Debt, NotificationBuilder>>builder()
        .put("CUSTOMER_SERVICE", debt -> notificationFactory.fromCustomerService(debt.getClientId()))
        .put("DEBT_COLLECTION", debt -> notificationFactory.fromDebtCollection(debt.getClientId()))
        .put("PRE_LEGAL", debt -> notificationFactory.fromPreLegal(debt.getClientId()))
        .put("LEGAL", debt -> notificationFactory.fromLegal(debt.getClientId()))
        .put("EXTRA_LEGAL", debt -> notificationFactory.fromExtraLegal(debt.getClientId()))
        .build();

    @Override
    public void handle(ActionContext context) {
        String cmsKey = context.getRequiredParam("cmsKey", String.class);
        Optional<String> sendFrom = context.getParam("sendFrom", String.class);
        Debt debt = context.getDebt();
        Map<String, Object> cmsContext = cmsModels.debtContext(debt.getId());
        sendFrom.filter(NOTIFICATION_PROVIDERS::containsKey).map(NOTIFICATION_PROVIDERS::get).orElse(d -> notificationFactory.fromLoan(d.getLoanId())).apply(debt)
            .loanId(debt.getLoanId())
            .debtId(debt.getId())
            .render(cmsKey, cmsContext)
            .send();
    }
}
