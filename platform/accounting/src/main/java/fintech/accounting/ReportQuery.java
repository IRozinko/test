package fintech.accounting;


import lombok.Data;

import java.time.LocalDate;

import static fintech.DateUtils.date;

@Data
public class ReportQuery {

    private LocalDate postDateTo;

    private LocalDate bookingDateFrom = date("2000-01-01");
    private LocalDate bookingDateTo = date("2100-01-01");

    private String accountCode;
    private Long paymentId;
    private Long loanId;
    private Long clientId;
}
