package fintech.lending.core.loan.spi;

import fintech.lending.core.loan.commands.RepayLoanCommand;

import java.util.List;

public interface RepaymentStrategy {

    List<Long> repay(RepayLoanCommand command);

}
