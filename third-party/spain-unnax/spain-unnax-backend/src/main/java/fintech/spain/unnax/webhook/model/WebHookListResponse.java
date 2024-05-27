package fintech.spain.unnax.webhook.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class WebHookListResponse {

    private int count;
    private String next;
    private String previous;
    private List<WebHookResult> results = new ArrayList<>();

    @Data
    @Accessors(chain = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class WebHookResult {
        private Long id;
        private String client;
        private String event;
        private String target;

        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
    }

    public Optional<WebHookResult> findWebHook(String event, String target) {
        return results.stream()
            .filter(wh -> wh.event.equals(event) && wh.target.equals(target))
            .findFirst();
    }

    public WebHookListResponse addResult(WebHookResult result) {
        results.add(result);
        return this;
    }
}
