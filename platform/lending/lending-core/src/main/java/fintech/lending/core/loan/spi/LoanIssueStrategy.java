package fintech.lending.core.loan.spi;

import fintech.lending.core.loan.commands.CreateLoanCommand;
import fintech.lending.core.loan.commands.IssueLoanCommand;

public interface LoanIssueStrategy {

    Long issue(IssueLoanCommand command);
    Long issue(CreateLoanCommand command);
}
