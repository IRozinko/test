package fintech.spain.alfa.product.strategy.penalty;

import com.google.common.collect.ImmutableSet;
import fintech.DateRange;
import fintech.DateUtils;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.strategy.spi.PenaltyStrategy;
import fintech.transactions.Balance;
import fintech.transactions.TransactionQuery;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static fintech.BigDecimalUtils.*;
import static fintech.lending.core.loan.LoanStatusDetail.*;
import static fintech.spain.alfa.product.utils.CostCalculationUtils.availableCosts;
import static fintech.transactions.TransactionQuery.byInstallment;
import static fintech.transactions.TransactionQuery.notVoidedByInstallment;
import static java.math.BigDecimal.ZERO;

@RequiredArgsConstructor
public class AlfaDailyPenaltyStrategy implements PenaltyStrategy {

    public static final CalculationType CALCULATION_TYPE = CalculationType.A;

    private static final Set<LoanStatusDetail> STATUSES_WITH_PENALTIES = ImmutableSet.of(ACTIVE, RESCHEDULED, BROKEN);

    private final Loan loan;
    private final TransactionService transactionService;
    private final DailyPenaltyStrategyProperties strategyProperties;

    @Override
    public BigDecimal calculate(Installment installment, LocalDate calculationDate) {
        if (!STATUSES_WITH_PENALTIES.contains(loan.getStatusDetail()) || loan.isPenaltySuspended()) {
            return amount(0);
        }

        LocalDate firstDelayDate = installment.getDueDate().plusDays(1);
        LocalDate maxDelayDate = firstDelayDate.plusDays(installment.getGracePeriodInDays());
        if (DateUtils.lt(calculationDate, maxDelayDate)) {
            return amount(0);
        }

        Balance loanBalance = transactionService.getBalance(TransactionQuery.byLoan(loan.getId()));
        BigDecimal maxPenalties = loan.isCompliantWithAEMIP()
            ? availableCosts(loanBalance)
            : loanBalance.getPrincipalDisbursed().add(loanBalance.getInterestApplied());

        if (goe(loanBalance.getPenaltyApplied(), maxPenalties)) {
            return amount(0);
        }

        Balance installmentBalance = transactionService.getBalance(byInstallment(installment.getId()));
        BigDecimal maxPossibleNewPenalties = max(ZERO, maxPenalties.subtract(installmentBalance.getPenaltyApplied()));


        BigDecimal totalPenalty = new DateRange(firstDelayDate, calculationDate).stream()
            .map(date -> dailyPenalty(installment.getId(), date))
            .reduce(ZERO, BigDecimal::add);

        BigDecimal newPenalty = max(totalPenalty.subtract(installmentBalance.getPenaltyApplied()), ZERO);
        return Optional.of(newPenalty)
            .map(amount -> limit(amount, maxPossibleNewPenalties))
            .map(amount -> amount.setScale(2, RoundingMode.HALF_UP))
            .orElse(ZERO);
    }

    private BigDecimal dailyPenalty(long installmentId, LocalDate date) {
        Balance balance = transactionService.getBalance(byInstallment(installmentId, date));
        BigDecimal paymentAdjustment = transactionService.findFirst(notVoidedByInstallment(installmentId, TransactionType.REPAYMENT, date))
            .map(tx -> tx.getPrincipalPaid().add(tx.getInterestPaid()))
            .orElse(ZERO);
        BigDecimal baseAmount = balance.getPrincipalDue()
            .add(balance.getInterestDue())
            .add(paymentAdjustment);
        return strategyProperties.getPenaltyRate()
            .divide(amount(100), 6, RoundingMode.HALF_UP)
            .multiply(baseAmount);
    }

}
