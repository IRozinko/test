package fintech.lending.core.loan.spi;

import fintech.lending.core.loan.commands.DisburseLoanCommand;

public interface DisbursementStrategy {

    Long disburse(DisburseLoanCommand command);
}
