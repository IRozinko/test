package fintech.lending.creditline.impl;

import fintech.lending.core.invoice.InvoiceService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.penalty.PenaltyStrategy;
import fintech.lending.core.product.ProductService;
import fintech.lending.creditline.settings.CreditLineProductSettings;
import fintech.transactions.Balance;
import fintech.transactions.BalanceQuery;
import fintech.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.isPositive;
import static fintech.lending.core.penalty.PenaltyStrategy.CalculatedPenalty.noPenalty;

@Component
public class BrokenLoanPenaltyStrategy implements PenaltyStrategy {

    @Autowired
    private LoanService loanService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InvoiceService invoiceService;

    public CalculatedPenalty calculate(Long loanId, LocalDate calculationDate) {
        Loan loan = loanService.getLoan(loanId);
        CreditLineProductSettings settings = productService.getSettings(loan.getProductId(), CreditLineProductSettings.class);

        BigDecimal penaltyRatePerDayInPercent = settings.getPenaltySettings().getPenaltyRatePerDayPercent();
        if (penaltyRatePerDayInPercent == null || !isPositive(penaltyRatePerDayInPercent)) {
            return noPenalty(String.format("No penalties, penalty rate per day not configured: [%s]", penaltyRatePerDayInPercent));
        }

        if (!loan.isOpen(LoanStatusDetail.BROKEN)) {
            return noPenalty(String.format("No penalties, loan is not broken: [%s]", loan));
        }

        long overdueDays = ChronoUnit.DAYS.between(loan.getMaturityDate(), calculationDate) + 1;
        if (overdueDays <= 0) {
            return noPenalty(String.format("No penalties, overdue days is [%s]", overdueDays));
        }
        long gracePeriodDays = settings.getPenaltySettings().getBrokenLoanGracePeriodInDays();
        if (overdueDays <= gracePeriodDays) {
            return noPenalty(String.format("No penalties, overdue days [%s] less than grace period [%s]", overdueDays, gracePeriodDays));
        }

        Balance loanBalance = transactionService.getBalance(BalanceQuery.byLoan(loanId, calculationDate));
        BigDecimal principal = loanBalance.getPrincipalDue();
        if (!isPositive(principal)) {
            return noPenalty(String.format("No penalties, principal due [%s]", principal));
        }

        BigDecimal penaltyLimit = loanBalance.getPrincipalDisbursed()
            .subtract(loanBalance.getPrincipalWrittenOff())
            .multiply(settings.getPenaltySettings().getMaxLimitOfPrincipalPercent())
            .divide(amount(100), 2, BigDecimal.ROUND_DOWN);
        BigDecimal maxPenaltyToApply = penaltyLimit.subtract(loanBalance.getPenaltyApplied());
        BigDecimal penaltyRate = penaltyRatePerDayInPercent.divide(amount(100), 9, BigDecimal.ROUND_DOWN);

        LocalDate firstPenaltyDate = loan.getMaturityDate().plusDays(gracePeriodDays);
        int penaltyDays = calculationDate.isEqual(firstPenaltyDate) ? (int) overdueDays : 1;

        BigDecimal totalPenaltyToApply = principal.multiply(amount(penaltyDays)).multiply(penaltyRate);
        BigDecimal penaltyAppliedBefore = loanBalance.getPenaltyApplied();
        BigDecimal newPenalty = maxPenaltyToApply.min(totalPenaltyToApply);

        if (!isPositive(newPenalty)) {
            return noPenalty(String.format("No penalties, penalty limit [%s], penalty applied before [%s], new penalty to apply [%s]", penaltyLimit, penaltyAppliedBefore, newPenalty));
        }

        return new CalculatedPenalty(newPenalty,
            String.format("Overdue days [%s], grace period [%s], principal due [%s]", overdueDays, gracePeriodDays, principal));
    }

}
