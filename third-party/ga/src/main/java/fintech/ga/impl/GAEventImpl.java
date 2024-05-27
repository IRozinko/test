package fintech.ga.impl;

import fintech.ga.events.GAEvent;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GAEventImpl implements GAEvent {

    private final Map<String, String> params;
    private final Map<String, String> unknownCidParams;
    private final Long clientId;

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getUnknownCidParams() {
        return unknownCidParams;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }
}
