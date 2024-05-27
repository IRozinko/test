package fintech.lending.core.loan.spi;

import fintech.lending.core.loan.commands.BreakLoanCommand;
import fintech.lending.core.loan.commands.UnBreakLoanCommand;

public interface BreakLoanStrategy {

    void breakLoan(BreakLoanCommand command);

    void unBreakLoan(UnBreakLoanCommand command);

}
