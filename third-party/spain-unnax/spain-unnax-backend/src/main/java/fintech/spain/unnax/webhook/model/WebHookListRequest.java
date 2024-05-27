package fintech.spain.unnax.webhook.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class WebHookListRequest {

    private int limit = 100;
    private String event;
    private String client;

    public Map<String, Object> toParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);
        Optional.ofNullable(event)
            .ifPresent(e -> params.put("event", e));
        Optional.ofNullable(client)
            .ifPresent(c -> params.put("client", c));
        return params;
    }
}
