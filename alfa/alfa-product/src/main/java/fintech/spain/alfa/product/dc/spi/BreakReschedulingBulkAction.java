package fintech.spain.alfa.product.dc.spi;

import fintech.TimeMachine;
import fintech.dc.model.Debt;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.spain.alfa.product.lending.LoanReschedulingService;
import fintech.spain.dc.command.BreakReschedulingCommand;
import fintech.spain.alfa.product.dc.DcFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BreakReschedulingBulkAction implements BulkActionHandler {

    @Autowired
    private DcFacade dcFacade;

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanReschedulingService loanReschedulingService;

    @Override
    public void handle(BulkActionContext context) {
        Debt debt = context.getDebt();
        Loan loan = loanService.getLoan(debt.getLoanId());
        if (loan.getStatusDetail() == LoanStatusDetail.RESCHEDULED) {
            dcFacade.breakRescheduling(new BreakReschedulingCommand()
                .setLoanId(debt.getLoanId())
                .setWhen(TimeMachine.today())
            );
        }
    }
}
