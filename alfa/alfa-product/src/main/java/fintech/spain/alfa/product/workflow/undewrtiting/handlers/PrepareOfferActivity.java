package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.TimeMachine;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class PrepareOfferActivity implements ActivityHandler {

    @Autowired
    private UnderwritingFacade underwritingFacade;

    @Override
    public ActivityResult handle(ActivityContext context) {
        Long applicationId = context.getWorkflow().getApplicationId();
        underwritingFacade.prepareOffer(applicationId, TimeMachine.today());

        Long agreementFileId = underwritingFacade.generateLoanAgreement(applicationId);
        context.setAttribute(Attributes.AGREEMENT_ATTACHMENT_ID, agreementFileId.toString());
        
        Long standardInformationFileId = underwritingFacade.generateStandardInformation(applicationId);
        context.setAttribute(Attributes.STANDARD_INFORMATON_ATTACHMENT_ID, standardInformationFileId.toString());

        return ActivityResult.resolution(Resolutions.OK, "");
    }
}
