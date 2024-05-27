package fintech.ga.events;

import java.util.Map;

public interface GAEvent {

    Map<String, String> getParams();
    Map<String, String> getUnknownCidParams();

    Long getClientId();
}
