package fintech.spain.alfa.product.dc.spi;

import fintech.TimeMachine;
import fintech.dc.model.Debt;
import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.BreakLoanCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BreakLoanAction implements ActionHandler {

    @Autowired
    private LoanService loanService;

    @Override
    public void handle(ActionContext context) {
        Debt debt = context.getDebt();
        BreakLoanCommand command = new BreakLoanCommand();
        command.setLoanId(debt.getLoanId());
        command.setWhen(TimeMachine.today());
        loanService.breakLoan(command);
    }
}
