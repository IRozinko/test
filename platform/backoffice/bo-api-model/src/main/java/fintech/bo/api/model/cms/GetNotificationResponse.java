package fintech.bo.api.model.cms;

import lombok.Data;

@Data
@Deprecated //use fintech.bo.api.model.cms.RenderNotificationResponse
public class GetNotificationResponse {

    private String emailSubject;
    private String emailBody;
    private String smsText;

}
