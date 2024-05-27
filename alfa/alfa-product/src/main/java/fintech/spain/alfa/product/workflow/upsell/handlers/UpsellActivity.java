package fintech.spain.alfa.product.workflow.upsell.handlers;

import fintech.TimeMachine;
import fintech.spain.alfa.product.lending.UpsellService;
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
public class UpsellActivity implements ActivityHandler {

    @Autowired
    private UpsellService upsellService;

    @Override
    public ActivityResult handle(ActivityContext context) {
        Long disbursementId = upsellService.issueUpsell(context.getWorkflow().getApplicationId(), TimeMachine.today());

        context.setAttribute(Attributes.DISBURSEMENT_ID, String.valueOf(disbursementId));

        return ActivityResult.resolution(Resolutions.OK, "");
    }
}
