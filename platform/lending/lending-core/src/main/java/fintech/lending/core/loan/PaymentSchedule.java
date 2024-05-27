package fintech.lending.core.loan;

import fintech.lending.core.PeriodUnit;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentSchedule {

    private Long id;
    private Long loanId;
    private Long clientId;
    private boolean latest;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private PeriodUnit periodUnit;
    private Long periodCount;
    private Long installments;
    private Long gracePeriodInDays;
    private PeriodUnit extensionPeriodUnit;
    private Long extensionPeriodCount;
    private boolean invoiceAppliedPenalty;
    private boolean invoiceAppliedInterest;
    private boolean invoiceAppliedFees;
    private boolean closeLoanOnPaid;
    private long baseOverdueDays;
}
