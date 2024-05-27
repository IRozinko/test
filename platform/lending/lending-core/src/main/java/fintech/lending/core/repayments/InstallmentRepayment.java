package fintech.lending.core.repayments;

import fintech.BigDecimalUtils;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import fintech.lending.core.util.TransactionBuilder;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.Balance;
import fintech.transactions.EntryBalance;
import fintech.transactions.TransactionEntryQuery;
import fintech.transactions.TransactionEntryType;
import fintech.transactions.TransactionQuery;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static fintech.lending.creditline.TransactionConstants.TRANSACTION_SUB_TYPE_INSTALLMENT_REPAYMENT;
import static fintech.lending.creditline.TransactionConstants.TRANSACTION_SUB_TYPE_RESCHEDULE_REPAYMENT;

@Component
public class InstallmentRepayment {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionBuilder transactionBuilder;

    @Autowired
    private LoanService loanService;

    public Optional<Long> repay(RepayLoanCommand command, Installment installment, RunningAmount runningAmount) {
        if (!runningAmount.isAmountLeft()) {
            return Optional.empty();
        }
        Balance balance = transactionService.getBalance(TransactionQuery.byInstallment(installment.getId()));
        if (BigDecimalUtils.isZero(balance.getTotalDue())) {
            return Optional.empty();
        }
        Loan loan = loanService.getLoan(command.getLoanId());

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.REPAYMENT);
        tx.setTransactionSubType(loan.getStatusDetail() == LoanStatusDetail.RESCHEDULED
            ? TRANSACTION_SUB_TYPE_RESCHEDULE_REPAYMENT : TRANSACTION_SUB_TYPE_INSTALLMENT_REPAYMENT);

        if (BigDecimalUtils.isPositive(balance.getFeeDue())) {
            feePayment(installment, runningAmount, tx);
        }
        tx.setPenaltyPaid(runningAmount.take(balance.getPenaltyDue()));
        tx.setInterestPaid(runningAmount.take(balance.getInterestDue()));
        tx.setPrincipalPaid(runningAmount.take(balance.getPrincipalDue()));

        tx.setComments(command.getComments());
        tx.setCashIn(runningAmount.getPaymentAmountUsed());
        tx.setOverpaymentUsed(runningAmount.getOverpaymentAmountUsed());
        tx.setInstallmentId(installment.getId());
        tx.setValueDate(command.getValueDate());
        if (BigDecimalUtils.isPositive(runningAmount.getPaymentAmountUsed())) {
            transactionBuilder.addPaymentValues(command.getPaymentId(), tx);
        }
        transactionBuilder.addLoanValues(loan, tx);
        Long txId = transactionService.addTransaction(tx);
        runningAmount.resetAmountUsed();
        return Optional.of(txId);
    }

    private void feePayment(Installment installment, RunningAmount runningAmount, AddTransactionCommand tx) {
        List<EntryBalance> entries = transactionService.getEntryBalance(TransactionEntryQuery.builder().type(TransactionEntryType.FEE).installmentId(installment.getId()).build());
        for (EntryBalance entryBalance : entries) {
            BigDecimal due = entryBalance.getAmountInvoiced().subtract(entryBalance.getAmountPaid());
            tx.addEntry(new AddTransactionCommand.TransactionEntry()
                .setType(entryBalance.getType())
                .setSubType(entryBalance.getSubType())
                .setAmountPaid(runningAmount.take(due)));
        }
    }

}
