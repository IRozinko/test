package fintech.lending.core.loan.events;

import lombok.Value;

import java.time.LocalDate;

@Value
public class LoanPaymentEvent {

    private long loanId;
    private LocalDate valueDate;
    private Long paymentId;

}
