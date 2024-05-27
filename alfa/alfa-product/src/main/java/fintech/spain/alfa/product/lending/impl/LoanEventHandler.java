package fintech.spain.alfa.product.lending.impl;

import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.VoidLoanCommand;
import fintech.payments.events.DisbursementVoidedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static fintech.TimeMachine.today;

@Component
@RequiredArgsConstructor
public class LoanEventHandler {

    private final LoanService loanService;

    @EventListener
    public void handleDisbursementVoided(DisbursementVoidedEvent event) {
        Loan loan = loanService.getLoan(event.getDisbursement().getLoanId());
        loanService.voidLoan(new VoidLoanCommand(loan.getId(), today()));
    }

}
