package fintech.lending.core.repayments;

import com.google.common.collect.ImmutableList;
import fintech.BigDecimalUtils;
import fintech.Validate;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import fintech.lending.core.util.TransactionBuilder;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.Balance;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.transactions.TransactionQuery.byLoan;

@Component
@RequiredArgsConstructor
public class LoanDueAmountsRepayment {

    private final LoanService loanService;
    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;

    public List<Long> repay(RepayLoanCommand command, RunningAmount runningAmount) {
        if (!runningAmount.isAmountLeft()) {
            return ImmutableList.of();
        }

        Loan loan = loanService.getLoan(command.getLoanId());
        Balance balance = transactionService.getBalance(byLoan(command.getLoanId()));

        if (BigDecimalUtils.isZero(balance.getTotalDue())) {
            return ImmutableList.of();
        }
        Validate.isZero(balance.getFeeDue(), "Loan fee due repayment not supported: [%s]", command);

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.REPAYMENT);
        tx.setTransactionSubType("DUE_AMOUNT_REPAYMENT");
        tx.setPenaltyPaid(runningAmount.take(balance.getPenaltyDue()));
        tx.setInterestPaid(runningAmount.take(balance.getInterestDue()));
        tx.setPrincipalPaid(runningAmount.take(balance.getPrincipalDue()));
        tx.setComments(command.getComments());
        tx.setCashIn(runningAmount.getPaymentAmountUsed());
        tx.setOverpaymentUsed(runningAmount.getOverpaymentAmountUsed());
        tx.setValueDate(command.getValueDate());
        if (BigDecimalUtils.isPositive(runningAmount.getPaymentAmountUsed())) {
            transactionBuilder.addPaymentValues(command.getPaymentId(), tx);
        }
        transactionBuilder.addLoanValues(loan, tx);
        Long txId = transactionService.addTransaction(tx);
        runningAmount.resetAmountUsed();
        return ImmutableList.of(txId);
    }
}
