package fintech.spain.alfa.product.workflow.dormants.handler;

import fintech.crm.client.model.PrestoDormantsResponse;
import fintech.spain.alfa.product.lending.LineOfCreditFacade;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class LocSendToPrestoActivity implements ActivityHandler {

    @Autowired
    private LineOfCreditFacade lineOfCreditFacade;

    @Override
    public ActivityResult handle(ActivityContext context) {
        PrestoDormantsResponse response = lineOfCreditFacade.sendClientToPresto(context.getClientId(), context.getWorkflow().getApplicationId());
        context.setAttribute(Attributes.LOC_REDIRECT_LINK, response.getLink());
        return ActivityResult.resolution(Resolutions.OK, "");
    }
}
