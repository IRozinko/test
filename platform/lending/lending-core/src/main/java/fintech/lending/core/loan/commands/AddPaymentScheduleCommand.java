package fintech.lending.core.loan.commands;

import fintech.lending.core.PeriodUnit;
import fintech.lending.core.loan.PaymentSchedule;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class AddPaymentScheduleCommand {

    private Long loanId;
    private LocalDate startDate;
    private PeriodUnit periodUnit;
    private Long periodCount;
    private Long installments = 0L;
    private Long gracePeriodInDays = 0L;
    private PeriodUnit extensionPeriodUnit;
    private Long extensionPeriodCount = 0L;
    private Long sourceTransactionId;

    private boolean invoiceAppliedPenalty;
    private boolean invoiceAppliedInterest;
    private boolean invoiceAppliedFees;
    private boolean closeLoanOnPaid;

    private long baseOverdueDays;

    public static AddPaymentScheduleCommand copyOf(PaymentSchedule schedule) {
        AddPaymentScheduleCommand command = new AddPaymentScheduleCommand();
        command.setLoanId(schedule.getLoanId());
        command.setStartDate(schedule.getStartDate());
        command.setPeriodUnit(schedule.getPeriodUnit());
        command.setPeriodCount(schedule.getPeriodCount());
        command.setInstallments(schedule.getInstallments());
        command.setGracePeriodInDays(schedule.getGracePeriodInDays());
        command.setExtensionPeriodUnit(schedule.getExtensionPeriodUnit());
        command.setExtensionPeriodCount(schedule.getExtensionPeriodCount());
        command.setInvoiceAppliedPenalty(schedule.isInvoiceAppliedPenalty());
        command.setInvoiceAppliedInterest(schedule.isInvoiceAppliedInterest());
        command.setInvoiceAppliedFees(schedule.isInvoiceAppliedFees());
        command.setCloseLoanOnPaid(schedule.isCloseLoanOnPaid());
        command.setBaseOverdueDays(schedule.getBaseOverdueDays());
        return command;
    }
}
