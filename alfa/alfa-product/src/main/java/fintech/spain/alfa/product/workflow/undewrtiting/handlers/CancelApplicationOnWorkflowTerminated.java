package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.workflow.spi.WorkflowListener;
import fintech.workflow.spi.WorkflowListenerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static fintech.lending.core.application.LoanApplicationStatusDetail.CANCELLED;
import static fintech.lending.core.application.LoanApplicationStatusDetail.REJECTED;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class CancelApplicationOnWorkflowTerminated implements WorkflowListener {

    @Autowired
    private LoanApplicationService applicationService;

    @Override
    public void handle(WorkflowListenerContext context) {
        Long applicationId = context.getWorkflow().getApplicationId();
        if (applicationId != null) {
            LoanApplication application = applicationService.get(applicationId);
            if (!REJECTED.equals(application.getStatusDetail()) && !CANCELLED.equals(application.getStatusDetail())) {
                applicationService.cancel(applicationId, context.getWorkflow().getTerminateReason());
            }
        }
    }
}
