package fintech.spain.alfa.web.services;

import fintech.FileHashId;
import fintech.JsonUtils;
import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatus;
import fintech.spain.alfa.web.models.AttachmentData;
import fintech.spain.alfa.web.models.DormantsLocOffer;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.web.api.models.AmortizationPayment;
import fintech.web.api.models.AmortizationPreviewResponse;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class WebDormantsLocService {

    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private ClientAttachmentService clientAttachmentService;
    @Autowired
    private LoanApplicationService applicationService;

    public DormantsLocOffer getOffer(Long clientId) {
        Workflow workflow = getLineOfCreditWorkflow(clientId);
        DormantsLocOffer offer = new DormantsLocOffer();
        offer.setApplicationId(workflow.getApplicationId());
        offer.setPayments(getPayments(workflow));
        offer.setCreditLimit(applicationService.get(workflow.getApplicationId()).getCreditLimit());
        if (workflow.getAttributes().get(Attributes.AGREEMENT_ATTACHMENT_ID) != null) {
            offer.setLoanAgreementAttachment(getAttachment(Long.valueOf(workflow.getAttributes().get(Attributes.AGREEMENT_ATTACHMENT_ID))));
        }
        if (workflow.getAttributes().get(Attributes.STANDARD_INFORMATON_ATTACHMENT_ID) != null) {
            offer.setStandardInformationAttachment(getAttachment(Long.valueOf(workflow.getAttributes().get(Attributes.STANDARD_INFORMATON_ATTACHMENT_ID))));
        }
        return offer;
    }


    private AttachmentData getAttachment(Long attachmentId) {
        Attachment attachment = clientAttachmentService.get(attachmentId);
        return new AttachmentData()
            .setFileId(FileHashId.encodeFileId(attachment.getClientId(), attachment.getFileId()))
            .setName(attachment.getName());
    }

    private List<AmortizationPayment> getPayments(Workflow workflow) {
        AmortizationPreviewResponse previewResponse = JsonUtils.readValue(workflow.getAttributes().get(Attributes.LOC_OFFER), AmortizationPreviewResponse.class);
        return previewResponse.getPayments();
    }

    private Workflow getLineOfCreditWorkflow(Long clientId) {
        Optional<LoanApplication> loanApplication = loanApplicationService.findLatest(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.OPEN));
        return loanApplication.map(LoanApplication::getWorkflowId).map(workflowService::getWorkflow).orElseThrow(IllegalStateException::new);
    }
}
