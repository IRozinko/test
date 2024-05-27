package fintech.ga;

import fintech.ga.events.GAEvent;


public interface GAService {

    void saveOrUpdateCookie(long clientId, String cookie, String userAgent);

    <E extends GAEvent> void sendEvent(E event);
}
