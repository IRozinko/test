package fintech.spain.unnax.db;

import fintech.db.BaseEntity;
import fintech.spain.unnax.webhook.model.CreateWebHookResponse;
import fintech.spain.unnax.webhook.model.WebHookListResponse;
import fintech.spain.unnax.webhook.model.WebHookType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "webhook", schema = Entities.SCHEMA)
public class WebHookEntity extends BaseEntity {

    private Long externalId;

    @Enumerated(EnumType.STRING)
    private WebHookType type;

    private Integer state;
    private String event;
    private String target;

    @Column(name = "webhook_created_at")
    private LocalDateTime webHookCreatedAt;

    @Column(name = "webhook_updated_at")
    private LocalDateTime webHookUpdatedAt;

    public WebHookEntity(CreateWebHookResponse response) {
        this.externalId = response.getId();
        this.type = WebHookType.valueOf(StringUtils.upperCase(response.getClient()));
        this.event = response.getEvent();
        this.target = response.getTarget();
        this.state = response.getState();
        this.webHookCreatedAt = response.getCreatedAt();
        this.webHookUpdatedAt = response.getUpdatedAt();
    }

    public WebHookEntity(WebHookListResponse.WebHookResult webHookResult) {
        this.externalId = webHookResult.getId();
        this.type = WebHookType.valueOf(StringUtils.upperCase(webHookResult.getClient()));
        this.event = webHookResult.getEvent();
        this.target = webHookResult.getTarget();
        this.webHookCreatedAt = webHookResult.getCreatedAt().toLocalDateTime();
        this.webHookUpdatedAt = webHookResult.getUpdatedAt().toLocalDateTime();
    }
}
