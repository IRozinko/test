package fintech.lending.core.repayments;

import com.google.common.collect.ImmutableList;
import fintech.AmountForPayment;
import fintech.BigDecimalUtils;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import fintech.lending.core.loan.events.LoanEarlyRepaymentEvent;
import fintech.lending.core.util.TransactionBuilder;
import fintech.transactions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static fintech.BigDecimalUtils.amountForPayment;
import static fintech.transactions.TransactionQuery.byLoan;

@Component
public class LoanOutstandingAmountsRepayment {

    private final LoanService loanService;
    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;
    private final ApplicationEventPublisher eventPublisher;
    @Autowired
    public LoanOutstandingAmountsRepayment(LoanService loanService, TransactionService transactionService,
                                           TransactionBuilder transactionBuilder, ApplicationEventPublisher eventPublisher) {
        this.loanService = loanService;
        this.transactionService = transactionService;
        this.transactionBuilder = transactionBuilder;
        this.eventPublisher = eventPublisher;
    }

    public List<Long> repay(RepayLoanCommand command, RunningAmount runningAmount) {
        if (!runningAmount.isAmountLeft()) {
            return ImmutableList.of();
        }

        Loan loan = loanService.getLoan(command.getLoanId());
        Balance balance = transactionService.getBalance(byLoan(command.getLoanId()));

        if (BigDecimalUtils.isZero(balance.getTotalOutstanding())) {
            return ImmutableList.of();
        }

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.REPAYMENT);
        tx.setTransactionSubType("OUTSTANDING_AMOUNT_REPAYMENT");

        AmountForPayment penaltyAmount = amountForPayment(balance.getPenaltyOutstanding());
        tx.setPenaltyPaid(runningAmount.take(penaltyAmount.getRoundedAmount()));
        tx.setPenaltyInvoiced(tx.getPenaltyPaid());
        tx.setPenaltyWrittenOff(penaltyAmount.getRoundingDifferenceAmount());

        AmountForPayment interestAmount = amountForPayment(balance.getInterestOutstanding());
        tx.setInterestPaid(runningAmount.take(interestAmount.getRoundedAmount()));
        tx.setInterestInvoiced(tx.getInterestPaid());
        tx.setInterestWrittenOff(interestAmount.getRoundingDifferenceAmount());

        addFeeEntries(command.getLoanId(), runningAmount, balance, tx);

        AmountForPayment principalAmount = amountForPayment(balance.getPrincipalOutstanding());
        tx.setPrincipalPaid(runningAmount.take(principalAmount.getRoundedAmount()));
        tx.setPrincipalInvoiced(tx.getPrincipalPaid());

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
        eventPublisher.publishEvent(new LoanEarlyRepaymentEvent(loan.getId()));

        return ImmutableList.of(txId);
    }

    private void addFeeEntries(Long loanId, RunningAmount runningAmount, Balance balance, AddTransactionCommand tx) {
        if (BigDecimalUtils.isPositive(balance.getFeeOutstanding())) {
            List<EntryBalance> entryBalance = transactionService.getEntryBalance(TransactionEntryQuery.byLoan(loanId, TransactionEntryType.FEE));
            entryBalance.forEach(e -> {
                if (runningAmount.isAmountLeft()) {
                    AmountForPayment feeAmount = amountForPayment(e.getAmountOutstanding());
                    if (!BigDecimalUtils.isZero(feeAmount.getRoundedAmount())) {
                        BigDecimal amountPaid = runningAmount.take(feeAmount.getRoundedAmount());
                        tx.addEntry(new AddTransactionCommand.TransactionEntry()
                            .setType(TransactionEntryType.FEE)
                            .setSubType(e.getSubType())
                            .setAmountPaid(amountPaid)
                            .setAmountWrittenOff(feeAmount.getRoundingDifferenceAmount())
                            .setAmountInvoiced(amountPaid));
                    }
                }
            });
        }
    }
}
