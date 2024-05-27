package fintech.lending.revolving.impl;

import fintech.AmountForPayment;
import fintech.Validate;
import fintech.lending.core.invoice.Invoice;
import fintech.lending.core.invoice.InvoiceQuery;
import fintech.lending.core.invoice.InvoiceService;
import fintech.lending.core.invoice.InvoiceStatusDetail;
import fintech.lending.core.invoice.commands.CloseInvoiceCommand;
import fintech.lending.core.loan.*;
import fintech.lending.core.loan.commands.AddInstallmentCommand;
import fintech.lending.core.loan.commands.AddLoanContractCommand;
import fintech.lending.core.loan.commands.BreakLoanCommand;
import fintech.lending.core.loan.commands.CancelInstallmentCommand;
import fintech.lending.core.loan.commands.UnBreakLoanCommand;
import fintech.lending.core.loan.spi.BreakLoanStrategy;
import fintech.transactions.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static fintech.BigDecimalUtils.amountForPayment;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Slf4j
@Component
public class RevolvingBreakLoanStrategy implements BreakLoanStrategy {

    private final InvoiceService invoiceService;
    private final TransactionService transactionService;
    private final LoanService loanService;
    private final ScheduleService scheduleService;

    @Autowired
    public RevolvingBreakLoanStrategy(InvoiceService invoiceService, TransactionService transactionService, LoanService loanService, ScheduleService scheduleService) {
        this.invoiceService = invoiceService;
        this.transactionService = transactionService;
        this.loanService = loanService;
        this.scheduleService = scheduleService;
    }

    @Override
    public void breakLoan(BreakLoanCommand command) {
        Loan loan = loanService.getLoan(command.getLoanId());
        Validate.isTrue(LoanStatusDetail.ACTIVE.equals(loan.getStatusDetail()),
            "Can not break loan [%s] in status [%s]", loan.getId(), loan.getStatusDetail());

        int baseOverdueDays = calculateBaseOverdueDays(command);
        cancelInvoicesAndInstallments(command, command.getLoanId());

        Long newContractId = scheduleService.addContract(new AddLoanContractCommand()
            .setLoanId(loan.getId())
            .setProductId(loan.getProductId())
            .setClientId(loan.getClientId())
            .setApplicationId(loan.getApplicationId())
            .setContractDate(command.getWhen())
            .setEffectiveDate(command.getWhen())
            .setMaturityDate(command.getWhen())
            .setNumberOfInstallments(1L)
            .setCloseLoanOnPaid(true)
            .setBaseOverdueDays(baseOverdueDays)
            .setSourceTransactionType(TransactionType.BREAK_LOAN)
        );

        AddInstallmentCommand addInstallmentCommand = new AddInstallmentCommand()
            .setTransactionType(TransactionType.BREAK_LOAN)
            .setContractId(newContractId)
            .setPeriodFrom(command.getWhen())
            .setPeriodTo(command.getWhen())
            .setDueDate(command.getWhen())
            .setValueDate(command.getWhen())
            .setInstallmentSequence(1L)
            .setInstallmentNumber(randomAlphabetic(8));

        Balance balance = transactionService.getBalance(BalanceQuery.byLoan(command.getLoanId()));

        // at this moment the due amounts should always be 0, because the invoices have been cancelled
        addInstallmentCommand.setPrincipalInvoiced(balance.getPrincipalOutstanding().subtract(balance.getPrincipalDue()));

        AmountForPayment interestAmount = amountForPayment(balance.getInterestOutstanding().subtract(balance.getInterestDue()));
        addInstallmentCommand.setInterestInvoiced(interestAmount.getRoundedAmount());
        addInstallmentCommand.setInterestWrittenOff(interestAmount.getRoundingDifferenceAmount());

        AmountForPayment penaltyAmount = amountForPayment(balance.getPenaltyOutstanding().subtract(balance.getPenaltyDue()));
        addInstallmentCommand.setPenaltyInvoiced(penaltyAmount.getRoundedAmount());
        addInstallmentCommand.setPenaltyWrittenOff(penaltyAmount.getRoundingDifferenceAmount());

        List<EntryBalance> feeEntries = transactionService.getEntryBalance(TransactionEntryQuery.builder()
            .loanId(command.getLoanId())
            .type(TransactionEntryType.FEE)
            .build());

        for (EntryBalance feeEntry : feeEntries) {
            AmountForPayment feeAmount = amountForPayment(feeEntry.getAmountOutstanding());

            AddTransactionCommand.TransactionEntry entry = new AddTransactionCommand.TransactionEntry();
            entry.setType(TransactionEntryType.FEE);
            entry.setSubType(feeEntry.getSubType());
            entry.setAmountInvoiced(feeAmount.getRoundedAmount());
            entry.setAmountWrittenOff(feeAmount.getRoundingDifferenceAmount());
            addInstallmentCommand.addEntry(entry);
        }

        loanService.addInstallment(addInstallmentCommand);
    }

    private int calculateBaseOverdueDays(BreakLoanCommand command) {
        Optional<Invoice> firstOpenInvoice = invoiceService.findFirstOpenInvoice(command.getLoanId());
        long baseOverdueDays = firstOpenInvoice.map(i -> ChronoUnit.DAYS.between(i.getDueDate(), command.getWhen())).orElse(0L);
        return Integer.max((int) baseOverdueDays, 0);
    }

    @Override
    public void unBreakLoan(UnBreakLoanCommand command) {
        throw new NotImplementedException("Un-break loan not supported");
    }

    private void cancelInvoicesAndInstallments(BreakLoanCommand command, Long loanId) {
        Contract contract = scheduleService.getCurrentContract(command.getLoanId());
        List<Installment> installments = scheduleService.findInstallments(InstallmentQuery.openContractInstallments(contract.getId()));
        installments.forEach(installment -> loanService.cancelInstallment(new CancelInstallmentCommand()
            .setBroken(true)
            .setCancelDate(command.getWhen())
            .setInstallmentId(installment.getId())
        ));

        invoiceService
            .find(InvoiceQuery.byLoanOpen(loanId))
            .stream()
            .sorted(comparing(Invoice::getPeriodFrom).reversed())
            .map(invoice -> CloseInvoiceCommand.builder()
                .invoiceId(invoice.getId())
                .date(command.getWhen())
                .statusDetail(InvoiceStatusDetail.BROKEN)
                .build())
            .forEachOrdered(invoiceService::closeInvoice);
    }
}
