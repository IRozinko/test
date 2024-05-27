package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.Validate;
import fintech.crm.attachments.AttachmentStatus;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class UpdateLoanAgreementStatus implements ActivityListener {

    @Autowired
    private ClientAttachmentService clientAttachmentService;

    @Override
    public void handle(ActivityContext context) {
        String attachmentId = context.getWorkflow().getAttributes().get(Attributes.AGREEMENT_ATTACHMENT_ID);
        Validate.notNull(attachmentId, "No agreement attachment id available");
        clientAttachmentService.updateStatus(Long.valueOf(attachmentId), AttachmentStatus.APPROVED, "");
    }
}
