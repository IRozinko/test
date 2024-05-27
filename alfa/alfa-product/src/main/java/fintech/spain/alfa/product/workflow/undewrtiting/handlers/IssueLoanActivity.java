package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.TimeMachine;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.spain.alfa.product.lending.LoanIssueResult;
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

import java.time.LocalDate;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class IssueLoanActivity implements ActivityHandler {

    @Autowired
    private UnderwritingFacade underwritingFacade;

    @Autowired
    private ClientAttachmentService clientAttachmentService;

    @Override
    public ActivityResult handle(ActivityContext context) {
        LocalDate issueDate = TimeMachine.today();
        Long applicationId = context.getWorkflow().getApplicationId();

        LoanIssueResult issueResult = underwritingFacade.issueLoan(applicationId, issueDate);

        context.setAttribute(Attributes.DISBURSEMENT_ID, issueResult.getDisbursementId().toString());
        context.updateLoanId(issueResult.getLoanId());

        saveLoanIdInLegalAttachments(context, issueResult);
        return ActivityResult.resolution(Resolutions.OK, "");
    }

    private void saveLoanIdInLegalAttachments(ActivityContext context, LoanIssueResult issueResult) {
        if (context.getWorkflow().hasAttribute(Attributes.AGREEMENT_ATTACHMENT_ID)) {
            clientAttachmentService.setLoanId(context.getWorkflow().attributeAsLong(Attributes.AGREEMENT_ATTACHMENT_ID), issueResult.getLoanId());
        }
        if (context.getWorkflow().hasAttribute(Attributes.STANDARD_INFORMATON_ATTACHMENT_ID)) {
            clientAttachmentService.setLoanId(context.getWorkflow().attributeAsLong(Attributes.STANDARD_INFORMATON_ATTACHMENT_ID), issueResult.getLoanId());
        }
    }
}
