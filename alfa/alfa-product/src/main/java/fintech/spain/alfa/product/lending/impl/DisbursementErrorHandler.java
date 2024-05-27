package fintech.spain.alfa.product.lending.impl;

import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.db.LoanEntity;
import fintech.lending.core.loan.db.LoanRepository;
import fintech.payments.events.DisbursementErrorOccurredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DisbursementErrorHandler {

    private final LoanRepository loanRepository;

    @EventListener
    @Transactional
    public void disbursementExported(DisbursementErrorOccurredEvent event) {
        LoanEntity loan = loanRepository.getRequired(event.getDisbursement().getLoanId());
        loan.open(LoanStatusDetail.DISBURSING);
        loanRepository.save(loan);
    }

}
