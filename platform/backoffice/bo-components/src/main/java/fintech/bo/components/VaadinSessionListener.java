package fintech.bo.components;

import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VaadinSessionListener {
    private static volatile int activeSessions = 0;

    public static class VaadinSessionInitListener implements SessionInitListener {

        @Override
        public void sessionInit(SessionInitEvent event) {
            incSessionCounter();
        }
    }

    public static class VaadinSessionDestroyListener implements SessionDestroyListener {

        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            decSessionCounter();
        }
    }

    private synchronized static void decSessionCounter() {
        activeSessions--;
        log.info("Current sessions: {}", activeSessions);
    }

    private synchronized static void incSessionCounter() {
        activeSessions++;
        log.info("Current sessions: {}", activeSessions);
    }
}
