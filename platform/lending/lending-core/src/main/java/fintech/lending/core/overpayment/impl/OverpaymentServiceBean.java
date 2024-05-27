package fintech.lending.core.overpayment.impl;

import fintech.Validate;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import fintech.lending.core.loan.commands.RepayLoanWithOverpaymentCommand;
import fintech.lending.core.overpayment.ApplyOverpaymentCommand;
import fintech.lending.core.overpayment.OverpaymentService;
import fintech.lending.core.overpayment.RefundOverpaymentCommand;
import fintech.lending.core.util.TransactionBuilder;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.Balance;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static fintech.BigDecimalUtils.amount;
import static fintech.transactions.TransactionQuery.byLoan;

@Component
@Slf4j
@Transactional
public class OverpaymentServiceBean implements OverpaymentService {

    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;
    private final LoanService loanService;

    @Autowired
    public OverpaymentServiceBean(TransactionService transactionService, TransactionBuilder transactionBuilder, LoanService loanService) {
        this.transactionService = transactionService;
        this.transactionBuilder = transactionBuilder;
        this.loanService = loanService;
    }

    @Override
    public Long applyOverpayment(ApplyOverpaymentCommand command) {
        Loan loan = loanService.getLoan(command.getLoanId());

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.OVERPAYMENT);
        tx.setComments(command.getComments());
        tx.setLoanId(command.getLoanId());
        tx.setClientId(command.getClientId());
        tx.setOverpaymentReceived(command.getAmount());
        tx.setCashIn(command.getAmount());
        tx.setDpd(loan.getOverdueDays());
        transactionBuilder.addPaymentValues(command.getPaymentId(), tx);
        return transactionService.addTransaction(tx);
    }

    @Override
    public Long refundOverpayment(RefundOverpaymentCommand command) {
        Loan loan = loanService.getLoan(command.getLoanId());

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.REFUND_OVERPAYMENT);
        tx.setComments(command.getComments());
        tx.setLoanId(command.getLoanId());
        tx.setClientId(command.getClientId());
        tx.setOverpaymentRefunded(command.getAmount());
        tx.setCashOut(command.getAmount());
        tx.setDpd(loan.getOverdueDays());
        transactionBuilder.addPaymentValues(command.getPaymentId(), tx);
        return transactionService.addTransaction(tx);
    }

    @Override
    public List<Long> userOverpayment(RepayLoanWithOverpaymentCommand command) {
        Validate.isPositive(command.getAmount(), "Overpayment amount should be positive");

        Loan loan = loanService.getLoan(command.getLoanId());
        log.info("Repaying loan with overpayment [{}], loan [{}]", command, loan);

        Balance balance = transactionService.getBalance(byLoan(loan.getId()));
        Validate.isLoe(command.getAmount(), balance.getOverpaymentAvailable(),
            "Amount [%s] exceeds available overpayment amount [%s]", command.getAmount(), balance.getOverpaymentAvailable());

        return loanService.repayLoan(new RepayLoanCommand()
            .setLoanId(loan.getId())
            .setPaymentAmount(amount(0))
            .setOverpaymentAmount(command.getAmount())
            .setValueDate(command.getWhen())
            .setComments("Using loan overpayment"));
    }
}
