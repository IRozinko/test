package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.payments.DisbursementService;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementStatusDetail;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.workflow.Workflow;
import fintech.workflow.spi.WorkflowListener;
import fintech.workflow.spi.WorkflowListenerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class CancelPendingDisbursement implements WorkflowListener {

    @Autowired
    private DisbursementService disbursementService;

    @Override
    public void handle(WorkflowListenerContext context) {
        Workflow workflow = context.getWorkflow();
        if (!workflow.hasAttribute(Attributes.DISBURSEMENT_ID)) {
            return;
        }

        long disbursementId = Long.parseLong(workflow.attribute(Attributes.DISBURSEMENT_ID));
        Disbursement disbursement = disbursementService.getDisbursement(disbursementId);
        if (DisbursementStatusDetail.PENDING.equals(disbursement.getStatusDetail())) {
            disbursementService.cancel(disbursementId, "CancelledByWorkflow");
        }
    }
}
