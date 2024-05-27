package fintech.spain.alfa.product.workflow.upsell.handlers;

import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.commands.SaveCreditLimitCommand;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
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
public class UpsellCreditLimitActivity implements ActivityHandler {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private LoanService loanService;

    @Override
    public ActivityResult handle(ActivityContext context) {
        Loan loan = loanService.getLoan(context.getWorkflow().getLoanId());

        loanApplicationService.saveCreditLimit(new SaveCreditLimitCommand(context.getWorkflow().getApplicationId(), loan.getCreditLimit().subtract(loan.getPrincipalDisbursed())));

        return ActivityResult.resolution(Resolutions.OK, "");
    }
}
