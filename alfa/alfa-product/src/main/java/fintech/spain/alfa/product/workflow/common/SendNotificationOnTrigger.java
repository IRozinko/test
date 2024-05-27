package fintech.spain.alfa.product.workflow.common;

import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.workflow.spi.TriggerContext;
import fintech.workflow.spi.TriggerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SendNotificationOnTrigger implements TriggerHandler {

    private final String cmsKey;

    @Autowired
    private CmsContextFactory cmsContextFactory;

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    public SendNotificationOnTrigger(String cmsKey) {
        this.cmsKey = cmsKey;
    }

    @Override
    public void handle(TriggerContext context) {
        notificationFactory.fromCustomerService(context.getWorkflow().getClientId())
            .loanId(context.getWorkflow().getLoanId())
            .loanApplicationId(context.getWorkflow().getApplicationId())
            .render(cmsKey, cmsContextFactory.getContext(context.getWorkflow()))
            .send();
    }
}
