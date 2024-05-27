package fintech.spain.dc.handler;

import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.lending.core.loan.LoanQuery.openLoans;

@Component
public class MostRecentDebtCondition implements ConditionHandler {

    private final LoanService loanService;

    @Autowired
    public MostRecentDebtCondition(LoanService loanService) {
        this.loanService = loanService;
    }

    @Override
    public boolean apply(ConditionContext context) {
        Long clientId = context.getDebt().getClientId();
        Loan mostRecentLoan = loanService.findLastLoanByIssueDate(openLoans(clientId))
            .orElseGet(() ->
                loanService.findLastLoanByIssueDate(LoanQuery.allLoans(clientId))
                    .orElseThrow(() -> new IllegalStateException("Cannot find most recent loan"))
            );
        return context.getDebt().getLoanId().equals(mostRecentLoan.getId());
    }
}
