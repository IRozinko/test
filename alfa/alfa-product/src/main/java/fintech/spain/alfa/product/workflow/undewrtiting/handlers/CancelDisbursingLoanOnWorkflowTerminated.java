package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.TimeMachine;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatus;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.VoidLoanCommand;
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
public class CancelDisbursingLoanOnWorkflowTerminated implements WorkflowListener {

    @Autowired
    private LoanService loanService;

    @Override
    public void handle(WorkflowListenerContext context) {
        Workflow workflow = context.getWorkflow();
        if (workflow.getLoanId() == null) {
            return;
        }

        Loan loan = loanService.getLoan(workflow.getLoanId());
        if (loan.getStatus().equals(LoanStatus.OPEN) && loan.getStatusDetail().equals(LoanStatusDetail.DISBURSING)) {
            loanService.voidLoan(new VoidLoanCommand()
                .setLoanId(loan.getId())
                .setVoidDate(TimeMachine.today()));
        }
    }
}
