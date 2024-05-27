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
import java.util.List;

import static fintech.BigDecimalUtils.amount;
import static fintech.transactions.TransactionQuery.notVoidedByLoan;
import static fintech.transactions.TransactionType.APPLY_FEE;
import static fintech.transactions.TransactionType.APPLY_PENALTY;

@Component
@Slf4j
public class MoneymanStrategy implements StrategyIdentifier {

    public static final String company = "Moneyman";
    private static final BigDecimal FEE_RATE = amount(60);
    private static final BigDecimal PENALTY_RATE = amount(1.3);
    private static final int MAX_PENALTY_DAYS = 149;

    @Autowired
    private LoanService loanService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PenaltyService penaltyService;

    @Override
    public void applyFee(Loan loan) {
        log.info("Trying to apply fee for loan: {}", loan.getId());
        LocalDate today = TimeMachine.today();
        ApplyFeeCommand command = ApplyFeeCommand.builder()
            .loanId(loan.getId())
            .amount(FEE_RATE)
            .valueDate(today)
            .build();
        if (today.isAfter(loan.getMaturityDate().plusDays(1))) {
            List<Transaction> txs = transactionService.findTransactions(notVoidedByLoan(loan.getId(), APPLY_FEE));
            if (txs.size() == 0) {
                loanService.applyFee(command);
            }
        }
    }

    @Override
    public void applyPenalty(Loan loan) {
        log.info("Trying to apply penalty for loan: {}", loan.getId());
        List<Transaction> txs = transactionService.findTransactions(notVoidedByLoan(loan.getId(), APPLY_PENALTY));
        if (txs.size() == 0) {
            BigDecimal amount = loan.getPrincipalDisbursed().multiply(PENALTY_RATE)
                .divide(BigDecimalUtils.amount(100),2, RoundingMode.HALF_DOWN);
            penaltyService.applyPenalty(loan.getId(), amount, loan.getMaturityDate(), loan.getMaturityDate().plusDays(MAX_PENALTY_DAYS));
        }
    }
}
