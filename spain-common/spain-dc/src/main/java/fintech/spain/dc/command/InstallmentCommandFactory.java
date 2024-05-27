package fintech.spain.dc.command;

import fintech.lending.core.loan.commands.AddInstallmentCommand;
import fintech.spain.dc.model.ReschedulingPreview;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.TransactionEntryType;

public class InstallmentCommandFactory {

    public static AddInstallmentCommand addInstallment(Long contractId, ReschedulingPreview.Item item, String installmentNumber) {
        AddInstallmentCommand addInstallmentCommand = new AddInstallmentCommand()
            .setContractId(contractId)
            .setInstallmentSequence(item.getInstallmentSequence())
            .setPeriodFrom(item.getPeriodFrom())
            .setPeriodTo(item.getPeriodTo())
            .setDueDate(item.getDueDate())
            .setGracePeriodInDays(item.getGracePeriodInDays())
            .setApplyPenalty(item.isApplyPenalty())
            .setValueDate(item.getPeriodFrom())
            .setPrincipalInvoiced(item.getPrincipalScheduled())
            .setInterestInvoiced(item.getInterestScheduled())
            .setPenaltyInvoiced(item.getPenaltyScheduled())
            .setGenerateInvoiceOnDate(item.getGenerateInvoiceOnDate())
            .setInstallmentNumber(installmentNumber);

        if (item.getFeeItems() != null) {
            item.getFeeItems().forEach(feeApplied ->
                addInstallmentCommand.addEntry(new AddTransactionCommand.TransactionEntry()
                    .setType(TransactionEntryType.FEE)
                    .setSubType(feeApplied.getType())
                    .setAmountApplied(feeApplied.getAmountApplied())
                    .setAmountInvoiced(feeApplied.getAmountScheduled())));
        }
        return addInstallmentCommand;
    }
}
