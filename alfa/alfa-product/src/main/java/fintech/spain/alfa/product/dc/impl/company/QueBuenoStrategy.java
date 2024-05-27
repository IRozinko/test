package fintech.spain.alfa.product.dc.impl.company;

import fintech.BigDecimalUtils;
import fintech.TimeMachine;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.ApplyFeeCommand;
import fintech.spain.alfa.product.dc.StrategyIdentifier;
import fintech.spain.alfa.product.lending.penalty.PenaltyService;
import fintech.transactions.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.calculateMaxPenaltyDays;

@Component
@Slf4j
public class QueBuenoStrategy implements StrategyIdentifier {

    public static final String company = "QueBueno";
    private static final BigDecimal PENALTY_RATE = amount(0.99);
    private static final Map<Integer, BigDecimal> feeSchedule = new HashMap<>();

    static {
        feeSchedule.put(1, amount(10));
        feeSchedule.put(15, amount(5));
        feeSchedule.put(75, amount(5));
    }
    /*
    *   Для Que Bueno:
        0,99% ежедневно *principal amount, начиная с Due date +1
        максимум 200% *principal amount
        10 eur 1 единоразово, начиная с Due date +1
        5 eur 1 единоразово, начиная с Due date +15
        5 eur 1 единоразово, начиная с Due date +75
    * */
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
        int daysToApplyPenalty = calculateMaxPenaltyDays(loan.getPrincipalDisbursed(), PENALTY_RATE, MAX_PENALTY_PRINCIPAL);
        BigDecimal amount = loan.getPrincipalDisbursed().multiply(PENALTY_RATE)
            .divide(BigDecimalUtils.amount(100),2, RoundingMode.HALF_DOWN);
        penaltyService.applyPenalty(loan.getId(), amount, loan.getMaturityDate(), loan.getMaturityDate().plusDays(daysToApplyPenalty));
    }

}
