package fintech.spain.alfa.product.dc.impl.company;

import fintech.BigDecimalUtils;
import fintech.TimeMachine;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.ApplyFeeCommand;
import fintech.spain.alfa.product.dc.StrategyIdentifier;
import fintech.spain.alfa.product.lending.penalty.PenaltyService;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.calculateMaxPenaltyDays;
import static fintech.transactions.TransactionQuery.notVoidedByLoan;
import static fintech.transactions.TransactionType.APPLY_PENALTY;

@Component
@Slf4j
public class WandooStrategy implements StrategyIdentifier {

    public static final String company = "Wandoo";

    private static final Map<Integer, BigDecimal> feeSchedule = new HashMap<>();

    static {
        feeSchedule.put(1, amount(20));
        feeSchedule.put(15, amount(15));
        feeSchedule.put(30, amount(15));
        feeSchedule.put(45, amount(25));
    }
    private static final BigDecimal PENALTY_RATE = amount(1.2);
    private static final BigDecimal MAX_PENALTY_PRINCIPAL = amount(2);

    @Autowired
    private LoanService loanService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PenaltyService penaltyService;

    @Override
    public void applyFee(Loan loan) {
        log.info("Trying to apply fee for loan: {} from {} company", loan.getId(), company);
        LocalDate today = TimeMachine.today();
        LocalDate maturityDate = loan.getMaturityDate();
        //TODO need to test
        for (Map.Entry<Integer, BigDecimal> entry : feeSchedule.entrySet()) {
            int daysAfterDueDate = entry.getKey();
            BigDecimal feeAmount = entry.getValue();
            LocalDate dueDatePlusDays = maturityDate.plusDays(daysAfterDueDate);

            if (today.isAfter(dueDatePlusDays)) {
                ApplyFeeCommand command = ApplyFeeCommand.builder()
                    .loanId(loan.getId())
                    .valueDate(dueDatePlusDays)
                    .amount(feeAmount)
                    .build();
                loanService.applyFee(command);
                break;
            }
        }
    }

    @Override
    public void applyPenalty(Loan loan) {
        log.info("Trying to apply penalty for loan: {} from {} company", loan.getId(), company);
        List<Transaction> txs = transactionService.findTransactions(notVoidedByLoan(loan.getId(), APPLY_PENALTY));
        if (txs.size() == 0) {
            int daysToApplyPenalty = calculateMaxPenaltyDays(loan.getPrincipalDisbursed(), PENALTY_RATE, MAX_PENALTY_PRINCIPAL);
            BigDecimal amount = loan.getPrincipalDisbursed().multiply(PENALTY_RATE)
                .divide(BigDecimalUtils.amount(100),2, RoundingMode.HALF_DOWN);
            penaltyService.applyPenalty(loan.getId(), amount, loan.getMaturityDate(), loan.getMaturityDate().plusDays(daysToApplyPenalty));
        }
    }
}
