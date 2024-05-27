package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.TimeMachine;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.AutoCompletePrecondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class RepaidLoanIssuedInLast90Days implements AutoCompletePrecondition {

    @Autowired
    private LoanService loanService;

    @Override
    public boolean isTrueFor(ActivityContext context) {
        return loanService.findLoans(LoanQuery.paidLoans(context.getClientId()).setIssueDateFrom(TimeMachine.today().minusDays(90))).stream()
            .max(Comparator.comparing(Loan::getIssueDate))
            .filter(loan -> loan.getOverdueDays() < 30)
            .isPresent();
    }
}
