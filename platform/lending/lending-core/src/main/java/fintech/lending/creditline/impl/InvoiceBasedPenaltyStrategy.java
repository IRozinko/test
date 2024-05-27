package fintech.lending.creditline.impl;

import fintech.lending.core.invoice.Invoice;
import fintech.lending.core.invoice.InvoiceService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatus;
import fintech.lending.core.penalty.PenaltyStrategy;
import fintech.lending.core.product.ProductService;
import fintech.lending.creditline.settings.CreditLineProductSettings;
import fintech.transactions.Balance;
import fintech.transactions.BalanceQuery;
import fintech.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.isPositive;
import static fintech.lending.core.invoice.InvoiceQuery.byLoan;
import static fintech.lending.core.penalty.PenaltyStrategy.CalculatedPenalty.noPenalty;
import static fintech.transactions.TransactionQuery.byInvoice;

@Component
public class InvoiceBasedPenaltyStrategy implements PenaltyStrategy {

    @Autowired
    private LoanService loanService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InvoiceService invoiceService;

    public CalculatedPenalty calculate(Long loanId, LocalDate calculationDate) {
        Loan loan = loanService.getLoan(loanId);
        CreditLineProductSettings settings = productService.getSettings(loan.getProductId(), CreditLineProductSettings.class);

        BigDecimal penaltyRatePerDayInPercent = settings.getPenaltySettings().getPenaltyRatePerDayPercent();
        if (penaltyRatePerDayInPercent == null || !isPositive(penaltyRatePerDayInPercent)) {
            return noPenalty(String.format("No penalties, penalty rate per day not configured: [%s]", penaltyRatePerDayInPercent));
        }

        if (loan.getStatus() == LoanStatus.CLOSED) {
            return noPenalty(String.format("No penalties, loan is not open: [%s]", loan.getStatusDetail()));
        }

        List<Invoice> loanInvoices = invoiceService.find(byLoan(loanId));
        if (loanInvoices.isEmpty()) {
            return noPenalty(String.format("No penalties, no loan invoices"));
        }

        Balance loanBalance = transactionService.getBalance(BalanceQuery.byLoan(loanId, calculationDate));
        BigDecimal penaltyLimit = loanBalance.getPrincipalDisbursed()
            .subtract(loanBalance.getPrincipalWrittenOff())
            .multiply(settings.getPenaltySettings().getMaxLimitOfPrincipalPercent())
            .divide(amount(100), 2, BigDecimal.ROUND_DOWN);
        BigDecimal maxPenaltyToApply = penaltyLimit.subtract(loanBalance.getPenaltyApplied());
        BigDecimal penaltyRate = penaltyRatePerDayInPercent.divide(amount(100), 9, BigDecimal.ROUND_DOWN);

        BigDecimal totalPenaltyToApply = BigDecimal.ZERO;
        StringBuilder comments = new StringBuilder();
        for (Invoice invoice : loanInvoices) {
            long gracePeriodDays = settings.getPenaltySettings().getInvoiceGracePeriodInDays();
            long overdueDays = ChronoUnit.DAYS.between(invoice.getDueDate(), calculationDate);
            if (overdueDays <= 0) {
                comments.append(String.format("No penalties for invoice [%s], " +
                    "overdue days is [%s]", invoice.getNumber(), overdueDays) + "\n");
                continue;
            }
            if (overdueDays < gracePeriodDays) {
                comments.append(noPenalty(String.format("No penalties for invoice [%s], " +
                    "overdue days [%s] less than grace period [%s]", invoice.getNumber(), overdueDays, gracePeriodDays)) + "\n");
                continue;
            }

            Balance invoiceBalance = transactionService.getBalance(byInvoice(invoice.getId(), calculationDate));
            BigDecimal amountDue = invoiceBalance.getPrincipalDue();
            if (!isPositive(amountDue)) {
                comments.append(noPenalty(String.format("No penalties for invoice [%s]," +
                    " principal due [%s]", invoice.getNumber(), amountDue)) + "\n");
                continue;
            }

            LocalDate firstPenaltyDate = invoice.getDueDate().plusDays(gracePeriodDays);
            int penaltyDays = calculationDate.isEqual(firstPenaltyDate) ? (int) overdueDays : 1;

            BigDecimal penaltyPerDay = amountDue.multiply(penaltyRate).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal calculatedInvoicePenalty = penaltyPerDay.multiply(amount(penaltyDays));
            BigDecimal invoicePenaltyToApply = maxPenaltyToApply.min(calculatedInvoicePenalty);
            totalPenaltyToApply = totalPenaltyToApply.add(invoicePenaltyToApply);
            maxPenaltyToApply = maxPenaltyToApply.subtract(invoicePenaltyToApply);
            comments.append(String.format("Penalty for invoice [%s] is [%s] on [%s], overdue days [%s], " +
                    "grace period [%s], amount due [%s]",
                invoice.getNumber(), invoicePenaltyToApply, calculationDate, overdueDays, gracePeriodDays, amountDue) + "\n");
        }

        return new CalculatedPenalty(totalPenaltyToApply, comments.toString());
    }

}
