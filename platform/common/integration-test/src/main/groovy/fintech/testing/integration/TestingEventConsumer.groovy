package fintech.testing.integration

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TestingEventConsumer {

    def events = []

    @EventListener
    void onAnyEvent(Object event) {
        events << event
    }

    int countOf(Class eventClass) {
        return events.clone().findAll { it.class.isAssignableFrom(eventClass) }.size()
    }

    boolean containsEvent(Class eventClass) {
        return countOf(eventClass) > 0;
    }

    public <T> T getEventOfType(Class<T> eventClass) {
        return events.find { it.class.isAssignableFrom(eventClass) }
    }

    void clear() {
        events = []
    }

}
