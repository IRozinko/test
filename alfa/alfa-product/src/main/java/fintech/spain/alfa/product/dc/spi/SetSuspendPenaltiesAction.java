package fintech.spain.alfa.product.dc.spi;

import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import fintech.lending.core.loan.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetSuspendPenaltiesAction implements ActionHandler {

    @Autowired
    private LoanService loanService;

    @Override
    public void handle(ActionContext context) {
        Long loanId = context.getDebt().getLoanId();
        boolean penaltySuspended = context.getRequiredParam("suspend", Boolean.class);
        loanService.setPenaltySuspended(loanId, penaltySuspended);
        log.info("Penalty suspended [{}] for loan {}", penaltySuspended, loanId);
    }
}
