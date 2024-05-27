package fintech.spain.alfa.product.lending.spi;

import com.google.common.collect.ImmutableList;
import fintech.Validate;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import fintech.lending.core.loan.spi.RepaymentStrategy;
import fintech.lending.core.overpayment.ApplyOverpaymentCommand;
import fintech.lending.core.overpayment.OverpaymentService;
import fintech.lending.core.repayments.RunningAmount;
import fintech.lending.core.util.TransactionBuilder;
import fintech.lending.creditline.TransactionConstants;
import fintech.spain.alfa.product.lending.LoanPrepayment;
import fintech.spain.alfa.product.lending.LoanServicingFacade;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.transactions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.eq;
import static fintech.BigDecimalUtils.isPositive;
import static fintech.BigDecimalUtils.isZero;
import static fintech.DateUtils.goe;
import static fintech.lending.core.loan.InstallmentQuery.openInstallments;
import static fintech.lending.creditline.TransactionConstants.TRANSACTION_SUB_TYPE_INSTALLMENT_REPAYMENT;
import static fintech.lending.creditline.TransactionConstants.TRANSACTION_SUB_TYPE_RESCHEDULE_REPAYMENT;
import static fintech.transactions.TransactionQuery.byInstallment;

@Component
public class AlfaRepaymentStrategy implements RepaymentStrategy {

    private static final int PREPAYMENT_GRACE_PERIOD_DAYS = 3;

    @Autowired
    private LoanServicingFacade loanServicingFacade;

    @Autowired
    private TransactionBuilder transactionBuilder;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private OverpaymentService overpaymentService;

    @Autowired
    private ScheduleService scheduleService;

    @Override
    public List<Long> repay(RepayLoanCommand command) {
        List<Long> prepayment = prepayment(command);

        return prepayment.isEmpty() ? distributePayment(command) : prepayment;
    }

    private List<Long> prepayment(RepayLoanCommand command) {
        RunningAmount runningAmount = new RunningAmount(command.getPaymentAmount(), command.getOverpaymentAmount());
        Optional<LoanPrepayment> maybePrepayment = findPrepayment(command.getLoanId(), command.getValueDate(), runningAmount.getAmountLeft());
        if (!maybePrepayment.isPresent()) {
            return ImmutableList.of();
        }

        Loan loan = loanService.getLoan(command.getLoanId());
        LoanPrepayment prepayment = maybePrepayment.get();
        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.REPAYMENT);
        tx.setTransactionSubType(TransactionConstants.TRANSACTION_SUB_TYPE_PREPAYMENT);
        tx.setValueDate(command.getValueDate());
        tx.setComments(command.getComments());

        tx.setInterestPaid(runningAmount.take(prepayment.getInterestToPay()));
        tx.setInterestWrittenOff(prepayment.getInterestToWriteOff());
        tx.setInterestInvoiced(prepayment.getInterestToWriteOff().negate());
        tx.setPrincipalPaid(runningAmount.take(prepayment.getPrincipalToPay()));
        tx.addEntry(new AddTransactionCommand.TransactionEntry()
            .setType(TransactionEntryType.FEE)
            .setSubType(AlfaConstants.PREPAYMENT_FEE_SUB_TYPE)
            .setAmountInvoiced(prepayment.getPrepaymentFeeToPay())
            .setAmountApplied(prepayment.getPrepaymentFeeToPay())
            .setAmountPaid(runningAmount.take(prepayment.getPrepaymentFeeToPay()))
        );

        tx.setCashIn(runningAmount.getPaymentAmountUsed());
        tx.setOverpaymentUsed(runningAmount.getOverpaymentAmountUsed());

        if (isPositive(runningAmount.getPaymentAmountUsed())) {
            transactionBuilder.addPaymentValues(command.getPaymentId(), tx);
        }
        transactionBuilder.addLoanValues(loan, tx);
        Long txId = transactionService.addTransaction(tx);
        return ImmutableList.of(txId);
    }

    private Optional<LoanPrepayment> findPrepayment(Long loanId, LocalDate valueDate, BigDecimal amount) {
        LocalDate minDate = valueDate.minusDays(PREPAYMENT_GRACE_PERIOD_DAYS);
        LocalDate date = valueDate;
        while (goe(date, minDate)) {
            LoanPrepayment prepayment = loanServicingFacade.calculatePrepayment(loanId, date);
            if (prepayment.isPrepaymentAvailable() && eq(prepayment.getTotalToPay(), amount)) {
                return Optional.of(prepayment);
            }
            date = date.minusDays(1);
        }

        return Optional.empty();
    }

    private List<Long> distributePayment(RepayLoanCommand command) {
        RunningAmount runningAmount = new RunningAmount(command.getPaymentAmount(), command.getOverpaymentAmount());
        List<Long> txIds = newArrayList();
        txIds.addAll(repayInstallments(command, runningAmount));
        if (runningAmount.isPaymentAmountLeft()) {
            txIds.add(applyOverpayment(command, runningAmount));
        }
        Validate.isTrue(!txIds.isEmpty(), "No transactions created by repayment strategy");
        return txIds;
    }

    private Long applyOverpayment(RepayLoanCommand command, RunningAmount runningAmount) {
        Loan loan = loanService.getLoan(command.getLoanId());
        Long txId = overpaymentService.applyOverpayment(ApplyOverpaymentCommand.builder()
            .loanId(loan.getId())
            .clientId(loan.getClientId())
            .paymentId(command.getPaymentId())
            .amount(runningAmount.takePayment(runningAmount.getPaymentAmountLeft()))
            .comments(command.getComments())
            .build());
        runningAmount.resetAmountUsed();
        return txId;
    }

    private List<Long> repayInstallments(RepayLoanCommand command, RunningAmount runningAmount) {
        List<Long> txIds = new ArrayList<>();
        for (Installment installment : scheduleService.findInstallments(openInstallments(command.getLoanId()))) {
            repayInstallment(command, installment, runningAmount).ifPresent(txIds::add);
        }
        return txIds;
    }

    private Optional<Long> repayInstallment(RepayLoanCommand command, Installment installment, RunningAmount runningAmount) {
        if (!runningAmount.isAmountLeft()) {
            return Optional.empty();
        }
        Loan loan = loanService.getLoan(command.getLoanId());
        Balance balance;
        if (loan.getStatusDetail().equals(LoanStatusDetail.RESCHEDULED)) {
            // find open installments and repaying them without using overpayment
            balance = transactionService.getBalance(byInstallment(installment.getId()));
        } else {
            balance = transactionService.getBalance(byInstallment(installment.getId(), command.getValueDate()));
        }
        if (isZero(balance.getTotalDue())) {
            return Optional.empty();
        }

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.REPAYMENT);
        tx.setTransactionSubType(loan.getStatusDetail() == LoanStatusDetail.RESCHEDULED
            ? TRANSACTION_SUB_TYPE_RESCHEDULE_REPAYMENT : TRANSACTION_SUB_TYPE_INSTALLMENT_REPAYMENT);

        if (isPositive(balance.getFeeDue())) {
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
        if (isPositive(runningAmount.getPaymentAmountUsed())) {
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
