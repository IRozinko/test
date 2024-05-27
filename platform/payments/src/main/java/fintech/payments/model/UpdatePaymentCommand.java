package fintech.payments.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class UpdatePaymentCommand {

    private long paymentId;
    private LocalDate valueDate;

}
