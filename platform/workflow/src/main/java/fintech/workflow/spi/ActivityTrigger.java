package fintech.workflow.spi;


import java.util.function.Function;

public abstract class ActivityTrigger implements Function<Object, Boolean> {

    private final Class<?> eventClass;

    public ActivityTrigger(Class<?> eventClass) {
        this.eventClass = eventClass;
    }

    @Override
    public abstract Boolean apply(Object input);

    public Class<?> getEventClass() {
        return eventClass;
    }
}
