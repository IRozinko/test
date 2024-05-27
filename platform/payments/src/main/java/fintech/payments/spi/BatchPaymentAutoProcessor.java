package fintech.payments.spi;


import java.time.LocalDate;

public interface BatchPaymentAutoProcessor {

    void autoProcessPending(int batchSize, LocalDate when);
}
