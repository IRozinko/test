package fintech.bo.api.model.cms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetNotificationRequest {
    private Long clientId;
    private Long debtId;
    private String key;
    private boolean render = true;
}
