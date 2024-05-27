package fintech.notification;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class NotificationQuery {

    private Long clientId;
    private String cmsKey;
    private LocalDateTime sentAfter;

    public static NotificationQuery byClientId(Long clientId, String cmsKey, LocalDateTime sentAfter) {
        return new NotificationQuery().setClientId(clientId).setCmsKey(cmsKey).setSentAfter(sentAfter);
    }

    public static NotificationQuery byClientId(Long clientId, String cmsKey) {
        return new NotificationQuery().setClientId(clientId).setCmsKey(cmsKey);
    }
}
