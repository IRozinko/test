package fintech.spain.alfa.product.workflow.upsell.handlers;

import fintech.crm.attachments.ClientAttachmentService;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
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
public class UpsellGenerateAgreementActivity implements ActivityHandler {

    @Autowired
    private UnderwritingFacade underwritingFacade;

    @Autowired
    private ClientAttachmentService clientAttachmentService;

    @Override
    public ActivityResult handle(ActivityContext context) {
        Long upsellAgreementFileId = underwritingFacade.generateUpsellAgreement(context.getWorkflow().getApplicationId());

        context.setAttribute(Attributes.UPSELL_AGREEMENT_ATTACHMENT_ID, upsellAgreementFileId.toString());

        clientAttachmentService.setLoanId(upsellAgreementFileId, context.getWorkflow().getLoanId());

        return ActivityResult.resolution(Resolutions.OK, "");
    }
}
