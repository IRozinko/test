package fintech.spain.alfa.product.dc.spi;

import fintech.TimeMachine;
import fintech.dc.model.Debt;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.BreakLoanCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BreakLoanBulkAction implements BulkActionHandler {

    @Autowired
    private LoanService loanService;

    @Override
    public void handle(BulkActionContext context) {
        Debt debt = context.getDebt();
        BreakLoanCommand command = new BreakLoanCommand();
        command.setLoanId(debt.getLoanId());
        command.setWhen(TimeMachine.today());
        loanService.breakLoan(command);
    }
}
