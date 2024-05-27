package fintech.spain.alfa.product.risk.rules.basic;

import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.rules.RuleBean;
import fintech.rules.model.Rule;
import fintech.rules.model.RuleContext;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.AlfaConstants;
import org.springframework.beans.factory.annotation.Autowired;

@RuleBean
public class NoOpenLoanRule implements Rule {

    @Autowired
    private LoanService loanService;

    @Override
    public RuleResult execute(RuleContext context, RuleResultBuilder builder) {
        int numberOfOpenLoans = loanService.findLoans(LoanQuery.openLoans(context.getClientId())).size();
        builder.addCheck("NumberOfOpenLoans", 0, numberOfOpenLoans);
        if (numberOfOpenLoans > 0) {
            return builder.reject(AlfaConstants.REJECT_REASON_CLIENT_HAS_OPEN_LOAN);
        } else {
            return builder.approve();
        }
    }

    @Override
    public String getName() {
        return "NoOpenLoan";
    }
}
