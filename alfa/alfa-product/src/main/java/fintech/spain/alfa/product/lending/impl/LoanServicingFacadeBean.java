package fintech.spain.alfa.product.lending.impl;

import fintech.Validate;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.util.TransactionBuilder;
import fintech.spain.alfa.product.lending.LoanPrepayment;
import fintech.spain.alfa.product.lending.LoanServicingFacade;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.isPositive;
import static fintech.lending.core.loan.LoanStatusDetail.ACTIVE;

@Transactional
@Component
@RequiredArgsConstructor
public class LoanServicingFacadeBean implements LoanServicingFacade {
    private final LoanService loanService;
    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;
    private final ScheduleService scheduleService;

    @Override
    public LoanPrepayment calculatePrepayment(Long loanId, LocalDate date) {
        Loan loan = loanService.getLoan(loanId);
        if (loan.getStatusDetail() != ACTIVE) {
            return LoanPrepayment.notAvailable(date);
        }
        if (isPositive(loan.getTotalPaid())) {
            return LoanPrepayment.notAvailable(date);
        }
        if (isPositive(loan.getPenaltyDue())) {
            return LoanPrepayment.notAvailable(date);
        }

        Contract contract = scheduleService.getCurrentContract(loanId);
        if (contract.getPeriodCount() <= 0) {
            return LoanPrepayment.notAvailable(date);
        }

        long days = ChronoUnit.DAYS.between(contract.getActiveFrom(), date);
        if (days >= contract.getPeriodCount()) {
            // prepayment not available on loan maturity date and beyond.
            return LoanPrepayment.notAvailable(date);
        }

        BigDecimal interestToPay = amount(0);
        if (days > 0) {
            interestToPay = loan.getInterestDue().multiply(amount(days)).divide(amount(contract.getPeriodCount()), 2, BigDecimal.ROUND_HALF_DOWN);
        }
        BigDecimal feeToPay = loan.getPrincipalDue().multiply(new BigDecimal("0.005")).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        return new LoanPrepayment()
            .setDate(date)
            .setPrepaymentAvailable(true)
            .setInterestToPay(interestToPay)
            .setInterestToWriteOff(loan.getInterestDue().subtract(interestToPay))
            .setPrincipalToPay(loan.getPrincipalDue())
            .setPrepaymentFeeToPay(feeToPay)
            .setTotalToPay(interestToPay.add(feeToPay).add(loan.getPrincipalDue()));
    }

    @Override
    public void renounceLoan(Long loanId, LocalDate date) {
        Loan loan = loanService.getLoan(loanId);
        Validate.isTrue(loan.getStatusDetail() == ACTIVE, "Loan is not active");
        Validate.isZero(loan.getTotalPaid(), "Loan already has paid amount");

        Contract contract = scheduleService.getCurrentContract(loan.getId());
        long days = ChronoUnit.DAYS.between(contract.getActiveFrom(), date);
        Validate.isTrue(days >= 0 && days <= 14, "Invalid renounce date");

        Installment installment = scheduleService.getFirstActiveInstallment(loan.getId());

        BigDecimal interestToPay = loan.getInterestDue().multiply(amount(days)).divide(amount(contract.getPeriodCount()), 2, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal interestToWriteOff = loan.getInterestDue().subtract(interestToPay);

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setTransactionType(TransactionType.RENOUNCE_LOAN);
        tx.setInstallmentId(installment.getId());
        tx.setValueDate(date);

        tx.setInterestWrittenOff(interestToWriteOff);
        tx.setInterestInvoiced(interestToWriteOff.negate());
        transactionBuilder.addLoanValues(loan, tx);
        transactionService.addTransaction(tx);
    }
}
