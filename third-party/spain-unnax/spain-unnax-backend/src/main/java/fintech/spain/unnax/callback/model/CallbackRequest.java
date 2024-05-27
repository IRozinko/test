package fintech.spain.unnax.callback.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import fintech.JsonUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Optional;


@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CallbackRequest {

    private String responseId;
    private String signature;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private String service;
    private String triggeredEvent;
    private String environment;
    private String traceIdentifier;

    private JsonNode data;

    public <T> CallbackRequest setData(T data) {
        this.data = JsonUtils.readTree(data);
        return this;
    }

    public String getDataAsText() {
        return Optional.ofNullable(data).map(JsonNode::toString).orElse("");
    }

    public <T> T getDataAsValue(Class<T> tClass) {
        return JsonUtils.treeToValue(data, tClass);
    }

}
