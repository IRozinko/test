package fintech.spain.unnax.webhook.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateWebHookResponse {

    private Long id;
    private String client;
    private String event;
    private String target;
    private Integer state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
