package fintech.spain.alfa.product.payments.processors;

import fintech.BigDecimalUtils;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatus;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import fintech.payments.model.Payment;
import fintech.payments.model.PaymentAutoProcessingResult;
import fintech.payments.model.PaymentType;
import fintech.payments.spi.PaymentAutoProcessor;
import fintech.spain.alfa.product.lending.LoanPrepayment;
import fintech.spain.alfa.product.lending.LoanServicingFacade;
import fintech.transactions.Balance;
import fintech.transactions.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static fintech.BigDecimalUtils.*;
import static fintech.payments.model.PaymentAutoProcessingResult.notProcessed;
import static fintech.payments.model.PaymentAutoProcessingResult.processed;
import static fintech.transactions.TransactionQuery.byLoan;

@Slf4j
@Component
public class LoanRepaymentProcessor implements PaymentAutoProcessor {

    public static final String EXTENSION_KEYWORD = "PRORROGA";

    private final LoanService loanService;
    private final LoanFinder loanFinder;
    private final LoanServicingFacade loanServicingFacade;
    private final TransactionService transactionService;

    @Autowired
    public LoanRepaymentProcessor(LoanService loanService, LoanFinder loanFinder, LoanServicingFacade loanServicingFacade, TransactionService transactionService) {
        this.loanService = loanService;
        this.loanFinder = loanFinder;
        this.loanServicingFacade = loanServicingFacade;
        this.transactionService = transactionService;
    }

    @Override
    public PaymentAutoProcessingResult autoProcessPayment(Payment payment, LocalDate when) {
        if (PaymentType.INCOMING != payment.getPaymentType()) {
            return notProcessed();
        }

        Optional<Loan> maybeLoan = loanFinder.findLoan(payment);
        if (!maybeLoan.isPresent()) {
            return notProcessed();
        }

        if (StringUtils.containsIgnoreCase(payment.getDetails(), EXTENSION_KEYWORD)) {
            return notProcessed();
        }

        Loan loan = maybeLoan.get();
        if (loan.getStatus() == LoanStatus.CLOSED || isZero(loan.getTotalDue())) {
            return notProcessed();
        }

        BigDecimal prepaymentAmount = calculatePrepaymentAmount(loan, payment);
        Balance balance = transactionService.getBalance(byLoan(loan.getId(), payment.getValueDate()));
        if (!BigDecimalUtils.eq(payment.getAmount(), balance.getTotalDue()) &&
            !BigDecimalUtils.eq(payment.getAmount(), prepaymentAmount)) {
            // should auto-repay only if amount exactly matches due amount or prepayment amount
            return notProcessed();
        }

        RepayLoanCommand command = new RepayLoanCommand();
        command.setLoanId(loan.getId());
        command.setPaymentId(payment.getId());
        command.setPaymentAmount(min(payment.getPendingAmount(), loan.getTotalDue()));
        command.setValueDate(payment.getValueDate());
        List<Long> transactionIds = loanService.repayLoan(command);

        return transactionIds.isEmpty() ? notProcessed() : processed();
    }

    private BigDecimal calculatePrepaymentAmount(Loan loan, Payment payment) {
        LoanPrepayment prepayment = loanServicingFacade.calculatePrepayment(loan.getId(), payment.getValueDate());
        if (!prepayment.isPrepaymentAvailable()) {
            return amount(-1);
        } else {
            return prepayment.getTotalToPay();
        }
    }
}
