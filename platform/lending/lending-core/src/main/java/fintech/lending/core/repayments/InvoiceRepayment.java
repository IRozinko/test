package fintech.lending.core.repayments;

import fintech.BigDecimalUtils;
import fintech.lending.core.invoice.Invoice;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import fintech.lending.core.util.TransactionBuilder;
import fintech.transactions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class InvoiceRepayment {

    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;
    private final ScheduleService scheduleService;
    private final LoanService loanService;

    @Autowired
    public InvoiceRepayment(TransactionService transactionService, TransactionBuilder transactionBuilder,
                            ScheduleService scheduleService, LoanService loanService) {
        this.transactionService = transactionService;
        this.transactionBuilder = transactionBuilder;
        this.scheduleService = scheduleService;
        this.loanService = loanService;
    }

    public Optional<Long> repay(RepayLoanCommand command, Invoice invoice, RunningAmount runningAmount) {
        if (!runningAmount.isAmountLeft()) {
            return Optional.empty();
        }
        Balance balance = transactionService.getBalance(TransactionQuery.byInvoice(invoice.getId()));
        if (BigDecimalUtils.isZero(balance.getTotalDue())) {
            return Optional.empty();
        }

        Loan loan = loanService.getLoan(command.getLoanId());
        List<Installment> openInstallments = scheduleService.findInstallments(InstallmentQuery.openInstallments(invoice.getLoanId()));

        AddTransactionCommand tx = new AddTransactionCommand();
        if (!openInstallments.isEmpty()) {
            tx.setInstallmentId(openInstallments.get(0).getId());
        }
        tx.setTransactionType(TransactionType.REPAYMENT);
        tx.setPenaltyPaid(runningAmount.take(balance.getPenaltyDue()));
        tx.setInterestPaid(runningAmount.take(balance.getInterestDue()));
        tx.setPrincipalPaid(runningAmount.take(balance.getPrincipalDue()));
        tx.setComments(command.getComments());
        tx.setInvoiceId(invoice.getId());
        tx.setValueDate(command.getValueDate());

        addFeeEntries(invoice, runningAmount, balance, tx);

        tx.setOverpaymentUsed(runningAmount.getOverpaymentAmountUsed());
        tx.setCashIn(runningAmount.getPaymentAmountUsed());
        if (BigDecimalUtils.isPositive(runningAmount.getPaymentAmountUsed())) {
            transactionBuilder.addPaymentValues(command.getPaymentId(), tx);
        }
        transactionBuilder.addLoanValues(loan, tx);
        Long txId = transactionService.addTransaction(tx);
        runningAmount.resetAmountUsed();
        return Optional.of(txId);
    }

    private void addFeeEntries(Invoice invoice, RunningAmount runningAmount, Balance balance, AddTransactionCommand tx) {
        if (BigDecimalUtils.isPositive(balance.getFeeDue())) {
            List<EntryBalance> entryBalance = transactionService.getEntryBalance(TransactionEntryQuery.byInvoice(invoice.getId(), TransactionEntryType.FEE));
            entryBalance.forEach(e -> tx.addEntry(new AddTransactionCommand.TransactionEntry()
                .setType(TransactionEntryType.FEE)
                .setSubType(e.getSubType())
                .setAmountPaid(runningAmount.take(e.getAmountDue()))
            ));
        }
    }
}
