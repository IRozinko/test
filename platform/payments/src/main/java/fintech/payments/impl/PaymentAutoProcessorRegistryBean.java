package fintech.payments.impl;

import com.google.common.collect.ImmutableList;
import fintech.Validate;
import fintech.payments.spi.PaymentAutoProcessor;
import fintech.payments.spi.PaymentAutoProcessorRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class PaymentAutoProcessorRegistryBean implements PaymentAutoProcessorRegistry {

    private List<PaymentAutoProcessor> processors = new ArrayList<>();

    @Override
    public void addProcessor(PaymentAutoProcessor processor) {
        Validate.notNull(processor);
        this.processors.add(processor);
    }

    @Override
    public void removeProcessors() {
        processors.clear();
    }

    @Override
    public List<PaymentAutoProcessor> getProcessors() {
        return ImmutableList.copyOf(processors);
    }
}
