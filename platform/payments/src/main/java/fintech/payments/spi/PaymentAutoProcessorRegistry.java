package fintech.payments.spi;

import java.util.List;

public interface PaymentAutoProcessorRegistry {

    void addProcessor(PaymentAutoProcessor processor);

    void removeProcessors();

    List<PaymentAutoProcessor> getProcessors();
}
