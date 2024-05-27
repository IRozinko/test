package fintech.spain.alfa.product.dc.spi;

import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.db.LoanEntity;
import fintech.lending.core.loan.db.LoanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChangeLoanStatusDetailAction implements ActionHandler {

    private final LoanRepository loanRepository;

    @Autowired
    public ChangeLoanStatusDetailAction(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public void handle(ActionContext context) {
        LoanStatusDetail newStatus = LoanStatusDetail.valueOf(context.getRequiredParam("statusDetail", String.class));
        LoanEntity loan = loanRepository.getRequired(context.getDebt().getLoanId());
        loan.open(newStatus);
        log.info("Loan status detail for loan {} changed to {}", context.getDebt().getLoanId(), newStatus);
    }
}
