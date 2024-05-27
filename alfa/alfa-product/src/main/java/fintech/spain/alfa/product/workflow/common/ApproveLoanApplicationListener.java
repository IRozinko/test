package fintech.spain.alfa.product.workflow.common;

import fintech.TimeMachine;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.commands.ApproveLoanApplicationCommand;
import fintech.workflow.spi.WorkflowListener;
import fintech.workflow.spi.WorkflowListenerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class ApproveLoanApplicationListener implements WorkflowListener {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public void handle(WorkflowListenerContext context) {
        ApproveLoanApplicationCommand command = new ApproveLoanApplicationCommand();
        command.setApproveDate(TimeMachine.today());
        command.setId(context.getWorkflow().getApplicationId());
        loanApplicationService.approve(command);
    }
}
