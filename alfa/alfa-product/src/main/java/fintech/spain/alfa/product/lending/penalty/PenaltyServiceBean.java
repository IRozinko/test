package fintech.spain.alfa.product.lending.penalty;

import fintech.DateRange;
import fintech.TimeMachine;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.commands.ApplyPenaltyCommand;
import fintech.lending.core.loan.events.LoanPaymentEvent;
import fintech.lending.core.util.TransactionBuilder;
import fintech.strategy.CalculationStrategyService;
import fintech.strategy.spi.PenaltyStrategy;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static fintech.BigDecimalUtils.ZERO;
import static fintech.BigDecimalUtils.isPositive;
import static fintech.lending.core.loan.InstallmentQuery.openInstallments;
import static fintech.transactions.TransactionQuery.notVoidedByLoan;

@Service
@Transactional
@RequiredArgsConstructor
public class PenaltyServiceBean implements PenaltyService {

    private final LoanService loanService;
    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;
    private final ScheduleService scheduleService;
    private final CalculationStrategyService strategyService;

    @EventListener
    public void handleLoanPaidEvent(LoanPaymentEvent event) {
        LocalDate nextPenaltyDate = event.getValueDate().plusDays(1);
        LocalDate today = TimeMachine.today();
        Loan loan = loanService.getLoan(event.getLoanId());
        if (nextPenaltyDate.isAfter(today) || loan.isClosed())
            return;

        voidPenalties(event.getLoanId(), nextPenaltyDate, today);
        applyPenalty(event.getLoanId(), nextPenaltyDate, today);
    }

    @Override
    public void applyPenalty(Long loanId, LocalDate from, LocalDate to) {
        new DateRange(from, to).stream().forEach(date -> applyPenalty(loanId, date));
    }

    @Override
    public void applyPenalty(Long loanId, BigDecimal amount, LocalDate from, LocalDate to) {
        new DateRange(from, to).stream().forEach(date -> applyPenalty(loanId, amount, date));
    }

    @Override
    public void applyPenalty(Long loanId, LocalDate calculationDate) {
        Optional<PenaltyStrategy> penaltyStrategyForLoan = strategyService.getPenaltyStrategyForLoan(loanId);
        if (!penaltyStrategyForLoan.isPresent()) {
            return;
        }

        List<Installment> openInstallments = scheduleService.findInstallments(openInstallments(loanId));

        openInstallments.stream()
            .map(i -> {
                BigDecimal amount = penaltyStrategyForLoan.get().calculate(i, calculationDate);
                return new InstallmentPenalty(i, amount, calculationDate);
            })
            .filter(ip -> isPositive(ip.amount))
            .map(this::toApplyPenaltyCommand)
            .forEach(loanService::applyPenalty);
    }

    @Override
    public void applyPenalty(Long loanId, BigDecimal amount, LocalDate date) {
        List<Installment> openInstallments = scheduleService.findInstallments(openInstallments(loanId));

        openInstallments.stream()
            .map(i -> new InstallmentPenalty(i, amount, date))
            .filter(ip -> isPositive(ip.amount))
            .map(this::toApplyPenaltyCommand)
            .forEach(loanService::applyPenalty);
    }

    @Override
    public BigDecimal calculatePenalty(Long loanId, LocalDate calculationDate) {
        Optional<PenaltyStrategy> penaltyStrategyForLoan = strategyService.getPenaltyStrategyForLoan(loanId);
        if (!penaltyStrategyForLoan.isPresent()) {
            return BigDecimal.ZERO;
        }

        List<Installment> openInstallments = scheduleService.findInstallments(openInstallments(loanId));

        return openInstallments
            .stream()
            .map(i -> penaltyStrategyForLoan.get().calculate(i, calculationDate))
            .reduce(ZERO, BigDecimal::add);
    }

    private void voidPenalties(long loanId, LocalDate from, LocalDate voidedDate) {
        transactionService.findTransactions(notVoidedByLoan(loanId, TransactionType.APPLY_PENALTY, from))
            .stream()
            .map(tx -> transactionBuilder.voidCommand(tx.getId(), voidedDate))
            .forEach(transactionService::voidTransaction);
    }


    private ApplyPenaltyCommand toApplyPenaltyCommand(InstallmentPenalty ip) {
        return new ApplyPenaltyCommand()
            .setAmount(ip.getAmount())
            .setAmountInvoiced(ip.getAmount())
            .setLoanId(ip.getLoanId())
            .setInstallmentId(ip.getInstallmentId())
            .setValueDate(ip.getCalculationDate());
    }

    @Value
    public static class InstallmentPenalty {

        InstallmentPenalty(Installment installment, BigDecimal amount, LocalDate calculationDate) {
            this.installmentId = installment.getId();
            this.loanId = installment.getLoanId();
            this.amount = amount;
            this.calculationDate = calculationDate;
        }

        long installmentId;
        long loanId;
        BigDecimal amount;
        LocalDate calculationDate;
    }

}
