package fintech.spain.alfa.product.workflow.common;


import fintech.workflow.spi.ActivityTrigger;

public class NoopEventTrigger extends ActivityTrigger {

    public NoopEventTrigger(Class<?> eventClass) {
        super(eventClass);
    }

    @Override
    public Boolean apply(Object input) {
        return true;
    }

    public static NoopEventTrigger event(Class<?> eventClass) {
        return new NoopEventTrigger(eventClass);
    }
}
