package fintech.bo.api.model.cms;

import lombok.Data;

@Data
public class RenderNotificationResponse {
    private String emailSubject;
    private String emailBody;
    private String smsText;
}
