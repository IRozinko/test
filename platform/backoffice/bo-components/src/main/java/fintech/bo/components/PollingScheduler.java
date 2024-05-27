package fintech.bo.components;

import com.google.common.eventbus.EventBus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PollingScheduler {

    private static final EventBus eventBus = new EventBus();

    @Scheduled(fixedRate = 2000L)
    public void run() {
        eventBus.post("tick");
    }

    public void subscribe(Object eventListener) {
        eventBus.register(eventListener);
    }

    public void unsubscribe(Object eventListener) {
        eventBus.unregister(eventListener);
    }

}
