package fintech.spain.alfa.product.workflow.common;

import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatus;
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
public class RejectApplicationWorkflowListener implements WorkflowListener {

    @Autowired
    private LoanApplicationService applicationService;

    @Override
    public void handle(WorkflowListenerContext context) {
        Long applicationId = context.getWorkflow().getApplicationId();
        if (applicationId != null) {
            LoanApplication application = applicationService.get(applicationId);
            if (application.getStatus() == LoanApplicationStatus.OPEN) {
                applicationService.reject(applicationId, context.getWorkflow().getTerminateReason());
            }
        }
    }
}
