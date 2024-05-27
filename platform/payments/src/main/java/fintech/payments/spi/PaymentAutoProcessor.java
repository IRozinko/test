package fintech.payments.spi;

import fintech.payments.model.Payment;
import fintech.payments.model.PaymentAutoProcessingResult;

import java.time.LocalDate;

public interface PaymentAutoProcessor {

    PaymentAutoProcessingResult autoProcessPayment(Payment payment, LocalDate when);
}
