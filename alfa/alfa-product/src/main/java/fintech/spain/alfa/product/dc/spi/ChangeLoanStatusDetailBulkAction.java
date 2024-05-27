package fintech.spain.alfa.product.dc.spi;

import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.db.LoanEntity;
import fintech.lending.core.loan.db.LoanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChangeLoanStatusDetailBulkAction implements BulkActionHandler {

    private final LoanRepository loanRepository;

    @Autowired
    public ChangeLoanStatusDetailBulkAction(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public void handle(BulkActionContext context) {
        LoanStatusDetail newStatus = LoanStatusDetail.valueOf(context.getRequiredParam("statusDetail", String.class));
        LoanEntity loan = loanRepository.getRequired(context.getDebt().getLoanId());
        loan.open(newStatus);
        log.info("Loan status detail for loan {} changed to {}", context.getDebt().getLoanId(), newStatus);
    }
}
