package fintech.lending.creditline.impl;

import fintech.Validate;
import fintech.lending.core.invoice.Invoice;
import fintech.lending.core.invoice.InvoiceQuery;
import fintech.lending.core.invoice.InvoiceService;
import fintech.lending.core.invoice.commands.GenerateInvoiceCommand;
import fintech.lending.core.invoice.commands.GeneratedInvoice;
import fintech.lending.core.invoice.db.InvoiceItemType;
import fintech.lending.core.invoice.spi.InvoiceNumberProvider;
import fintech.lending.core.invoice.spi.InvoicingStrategy;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatus;
import fintech.lending.core.product.ProductService;
import fintech.lending.creditline.TransactionConstants;
import fintech.lending.creditline.settings.CreditLineInvoiceSettings;
import fintech.lending.creditline.settings.CreditLineProductSettings;
import fintech.transactions.Balance;
import fintech.transactions.EntryBalance;
import fintech.transactions.TransactionEntryQuery;
import fintech.transactions.TransactionEntryType;
import fintech.transactions.TransactionQuery;
import fintech.transactions.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.amountForPayment;
import static fintech.BigDecimalUtils.isZero;
import static fintech.BigDecimalUtils.lt;
import static fintech.BigDecimalUtils.min;
import static fintech.BigDecimalUtils.percentageOfAmount;
import static fintech.DateUtils.goe;
import static fintech.transactions.TransactionQuery.byLoan;

@Slf4j
@Component
public class CreditLineInvoicingStrategy implements InvoicingStrategy {

    private final LoanService loanService;
    private final TransactionService transactionService;
    private final ProductService productService;
    private final InvoiceService invoiceService;
    private final InvoiceNumberProvider invoiceNumberProvider;

    @Autowired
    public CreditLineInvoicingStrategy(LoanService loanService, TransactionService transactionService, ProductService productService,
                                       InvoiceService invoiceService, InvoiceNumberProvider invoiceNumberProvider) {
        this.loanService = loanService;
        this.transactionService = transactionService;
        this.productService = productService;
        this.invoiceService = invoiceService;
        this.invoiceNumberProvider = invoiceNumberProvider;
    }

    @Override
    public List<GeneratedInvoice.GeneratedInvoiceItem> calculate(Long loanId, LocalDate periodFrom, LocalDate periodTo) {
        List<GeneratedInvoice.GeneratedInvoiceItem> items = newArrayList();

        items.addAll(calculateInterest(loanId, periodTo));
        items.addAll(calculatePenalties(loanId, periodTo));
        items.addAll(calculateDisbursementFees(loanId, periodTo, TransactionConstants.TRANSACTION_SUB_TYPE_FIRST_DISBURSEMENT_FEE));
        items.addAll(calculateDisbursementFees(loanId, periodTo, TransactionConstants.TRANSACTION_SUB_TYPE_REPEATED_DISBURSEMENT_FEE));
        items.addAll(calculatePrincipal(loanId, periodFrom, periodTo, items));

        return items;
    }

    @Override
    public Long generateInvoice(GenerateInvoiceCommand command) {
        Loan loan = loanService.getLoan(command.getLoanId());
        log.info("Generating invoice for [{}] date to [{}]", loan, command.getDateTo());
        Validate.isTrue(loan.getStatus() == LoanStatus.OPEN, "Required open loan");

        GeneratedInvoice generatedInvoice = createInvoice(command);
        generatedInvoice.setItems(calculate(loan.getId(), generatedInvoice.getPeriodFrom(), generatedInvoice.getPeriodTo()));

        return invoiceService.createInvoice(generatedInvoice);
    }

    private GeneratedInvoice createInvoice(GenerateInvoiceCommand command) {
        Loan loan = loanService.getLoan(command.getLoanId());
        CreditLineInvoiceSettings invoiceSettings = productService.getSettings(
            loan.getProductId(), CreditLineProductSettings.class).getInvoiceSettings();

        Optional<LocalDate> maxPeriodTo = invoiceService.find(InvoiceQuery.byLoan(loan.getId())).stream()
            .map(Invoice::getPeriodTo)
            .max(LocalDate::compareTo);

        LocalDate periodFrom = maxPeriodTo.map(localDate -> localDate.plusDays(1)).orElseGet(loan::getFirstDisbursementDate);
        LocalDate periodTo = command.getDateTo();
        LocalDate invoiceDate = command.getInvoiceDate();
        LocalDate dueDate = firstNonNull(command.getDueDate(), invoiceDate.plusDays(invoiceSettings.getDueDays()));

        Validate.isTrue(goe(periodTo, periodFrom), "Invalid invoice period from [%s] to [%s]", periodFrom, periodTo);
        Validate.isTrue(goe(command.getDateTo(), periodFrom), "Invoice already exists for period [%s]", periodFrom);

        return GeneratedInvoice.builder()
            .invoiceDate(invoiceDate)
            .periodFrom(periodFrom)
            .periodTo(periodTo)
            .dueDate(dueDate)
            .number(invoiceNumberProvider.nextInvoiceNumber(loan.getId()))
            .loanId(loan.getId())
            .productId(loan.getProductId())
            .clientId(loan.getClientId())
            .generateFile(command.isGenerateFile())
            .sendFile(command.isSendFile())
            .membershipLevelChecked(command.getMembershipLevelChecked())
            .manual(command.isManual())
            .build();
    }

    private List<GeneratedInvoice.GeneratedInvoiceItem> calculateDisbursementFees(Long loanId, LocalDate when, String transactionSubType) {
        List<GeneratedInvoice.GeneratedInvoiceItem> items = newArrayList();

        List<EntryBalance> entries = transactionService.getEntryBalance(TransactionEntryQuery.builder()
            .loanId(loanId)
            .valueDateTo(when)
            .type(TransactionEntryType.FEE)
            .subType(Collections.singletonList(transactionSubType))
            .build());

        BigDecimal feeApplied = entries.stream().map(EntryBalance::getAmountApplied).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal feeWrittenOff = entries.stream().map(EntryBalance::getAmountWrittenOff).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal feeInvoiced = entries.stream().map(EntryBalance::getAmountInvoiced).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal feeOutstanding = entries.stream().map(EntryBalance::getAmountOutstanding).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal unInvoicedFee = feeApplied
            .subtract(feeWrittenOff)
            .subtract(feeInvoiced);
        BigDecimal feePayable = min(unInvoicedFee, feeOutstanding);
        if (!isZero(feePayable)) {
            GeneratedInvoice.GeneratedInvoiceItem feeItem = new GeneratedInvoice.GeneratedInvoiceItem();
            feeItem.setType(InvoiceItemType.FEE);
            feeItem.setSubType(transactionSubType);
            feeItem.setAmount(feePayable);
            items.add(feeItem);
        }

        return items;
    }

    private List<GeneratedInvoice.GeneratedInvoiceItem> calculateInterest(Long loanId, LocalDate when) {
        List<GeneratedInvoice.GeneratedInvoiceItem> items = newArrayList();
        Balance loanBalanceUntilDate = transactionService.getBalance(byLoan(loanId, when));
        BigDecimal unInvoicedInterest = loanBalanceUntilDate.getInterestApplied()
            .subtract(loanBalanceUntilDate.getInterestWrittenOff())
            .subtract(loanBalanceUntilDate.getInterestInvoiced());
        BigDecimal interestPayable = min(unInvoicedInterest, loanBalanceUntilDate.getInterestOutstanding());
        if (!isZero(interestPayable)) {
            GeneratedInvoice.GeneratedInvoiceItem interestItem = new GeneratedInvoice.GeneratedInvoiceItem();
            interestItem.setType(InvoiceItemType.INTEREST);
            interestItem.setAmount(interestPayable);
            items.add(interestItem);
        }

        return items;
    }

    private List<GeneratedInvoice.GeneratedInvoiceItem> calculatePenalties(Long loanId, LocalDate when) {
        List<GeneratedInvoice.GeneratedInvoiceItem> items = newArrayList();
        Balance loanBalanceUntilDate = transactionService.getBalance(byLoan(loanId, when));
        BigDecimal unInvoicedPenalty = loanBalanceUntilDate.getPenaltyApplied()
            .subtract(loanBalanceUntilDate.getPenaltyWrittenOff())
            .subtract(loanBalanceUntilDate.getPenaltyInvoiced());
        BigDecimal penaltyPayable = min(unInvoicedPenalty, loanBalanceUntilDate.getPenaltyOutstanding());
        if (!isZero(penaltyPayable)) {
            GeneratedInvoice.GeneratedInvoiceItem penaltyItem = new GeneratedInvoice.GeneratedInvoiceItem();
            penaltyItem.setType(InvoiceItemType.PENALTY);
            penaltyItem.setAmount(penaltyPayable);
            items.add(penaltyItem);
        }

        return items;
    }

    private List<GeneratedInvoice.GeneratedInvoiceItem> calculatePrincipal(Long loanId, LocalDate periodFrom, LocalDate periodTo, List<GeneratedInvoice.GeneratedInvoiceItem> otherItems) {
        List<GeneratedInvoice.GeneratedInvoiceItem> items = newArrayList();

        Balance loanBalanceInPeriod = transactionService.getBalance(TransactionQuery.builder()
            .loanId(loanId)
            .valueDateFrom(periodFrom)
            .valueDateTo(periodTo)
            .build());
        BigDecimal totalInvoicedWithoutPrincipal = amountForPayment(otherItems.stream()
            .map(GeneratedInvoice.GeneratedInvoiceItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .add(loanBalanceInPeriod.getInterestInvoiced())
            .add(loanBalanceInPeriod.getFeeInvoiced())
            .add(loanBalanceInPeriod.getPenaltyInvoiced()))
            .getRoundedAmount();
        Balance loanBalanceToPeriod = transactionService.getBalance(byLoan(loanId, periodTo));
        BigDecimal principalOutstanding = loanBalanceToPeriod.getPrincipalOutstanding();
        BigDecimal uninvoicedPrincipal = principalOutstanding.subtract(loanBalanceToPeriod.getPrincipalDue());

        BigDecimal expectedPrincipal =
            percentageOfAmount(invoiceSettings(loanId).getPrincipalAmountPercentage(), principalOutstanding);

        BigDecimal total = totalInvoicedWithoutPrincipal.add(expectedPrincipal);
        BigDecimal principalPayable;
        BigDecimal minimalInvoiceAmount = invoiceSettings(loanId).getMinAmount();
        if (lt(total, minimalInvoiceAmount)) {
            principalPayable = min(principalOutstanding, minimalInvoiceAmount.subtract(totalInvoicedWithoutPrincipal));
        } else {
            principalPayable = expectedPrincipal;
        }
        principalPayable = min(uninvoicedPrincipal, principalPayable);

        if (!isZero(principalPayable)) {
            GeneratedInvoice.GeneratedInvoiceItem principalItem = new GeneratedInvoice.GeneratedInvoiceItem();
            principalItem.setType(InvoiceItemType.PRINCIPAL);
            principalItem.setAmount(principalPayable);
            items.add(principalItem);
        }

        return items;
    }

    private CreditLineInvoiceSettings invoiceSettings(Long loanId) {
        Loan loan = loanService.getLoan(loanId);
        return productService.getSettings(
            loan.getProductId(), CreditLineProductSettings.class).getInvoiceSettings();
    }

}
