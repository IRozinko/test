package fintech.spain.alfa.product.dc.spi;

import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PenaltiesSuspendedCondition implements ConditionHandler {

    @Autowired
    private LoanService loanService;

    @Override
    public boolean apply(ConditionContext context) {
        Loan loan = loanService.getLoan(context.getDebt().getLoanId());
        Boolean penaltiesSuspended = context.getRequiredParam("penaltiesSuspended", Boolean.class);
        return loan.isPenaltySuspended() == penaltiesSuspended;
    }
}
