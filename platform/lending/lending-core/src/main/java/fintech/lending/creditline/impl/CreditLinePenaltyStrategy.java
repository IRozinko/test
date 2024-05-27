package fintech.lending.creditline.impl;

import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.penalty.PenaltyStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static fintech.DateUtils.goe;

@Component
public class CreditLinePenaltyStrategy implements PenaltyStrategy {
    
    @Autowired
    private BrokenLoanPenaltyStrategy brokenLoanPenaltyStrategy;

    @Autowired
    private InvoiceBasedPenaltyStrategy invoiceBasedPenaltyStrategy;

    @Autowired
    private LoanService loanService;

    @Override
    public CalculatedPenalty calculate(Long loanId, LocalDate when) {
        Loan loan = loanService.getLoan(loanId);
        if (loan.getBrokenDate() != null && goe(when, loan.getBrokenDate())) {
            return brokenLoanPenaltyStrategy.calculate(loanId, when);
        } else {
            return invoiceBasedPenaltyStrategy.calculate(loanId, when);
        }
    }

}
