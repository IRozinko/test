package fintech.spain.alfa.product.workflow.upsell.handlers;

import com.google.common.collect.Lists;
import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.workflow.WorkflowService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class UpsellOfferEmailActivity implements ActivityHandler {

    @Autowired
    private UpsellGenerateAgreementActivity upsellGenerateAgreementActivity;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private ClientAttachmentService clientAttachmentService;

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    @Autowired
    private AlfaCmsModels cmsModels;

    @Override
    public ActivityResult handle(ActivityContext context) {
        upsellGenerateAgreementActivity.handle(context);

        LoanApplication loanApplication = loanApplicationService.get(context.getWorkflow().getApplicationId());

        Attachment attachment = clientAttachmentService.get(Long.valueOf(workflowService.getWorkflow(context.getWorkflow().getId()).attribute(Attributes.UPSELL_AGREEMENT_ATTACHMENT_ID)));

        notificationFactory.fromCustomerService(loanApplication.getClientId())
            .emailAttachmentFileIds(Lists.newArrayList(attachment.getFileId()))
            .loanApplicationId(loanApplication.getId())
            .render(CmsSetup.APPROVE_UPSELL_OFFER_EMAIL, cmsModels.applicationContext(loanApplication.getId()))
            .send();

        return ActivityResult.resolution(Resolutions.OK, "");
    }
}
