package fintech.spain.alfa.product.lending;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class LoanRescheduling {

    private Long id;
    private Long loanId;
    private LoanReschedulingStatus status;
    private LocalDate expireDate;
    private LocalDate rescheduleDate;
    private int numberOfPayments;
    private int repaymentDueDays;
    private int gracePeriodDays;

}
