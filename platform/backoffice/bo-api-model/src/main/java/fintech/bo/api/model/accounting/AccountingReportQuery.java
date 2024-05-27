package fintech.bo.api.model.accounting;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountingReportQuery {

    private LocalDate bookingDateFrom;
    private LocalDate bookingDateTo;
    private Long paymentId;
    private Long loanId;
    private Long clientId;
    private Long accountId;
}
