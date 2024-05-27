package fintech.spain.alfa.product.workflow.upsell.handlers;

import fintech.TimeMachine;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
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
public class UpsellPrepareOfferActivity implements ActivityHandler {

    @Autowired
    private UnderwritingFacade underwritingFacade;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public ActivityResult handle(ActivityContext context) {
        LoanApplication loanApplication = loanApplicationService.get(context.getWorkflow().getApplicationId());
        underwritingFacade.prepareOffer(loanApplication.getId(), TimeMachine.today());
        return ActivityResult.resolution(Resolutions.OK, "");
    }

}
