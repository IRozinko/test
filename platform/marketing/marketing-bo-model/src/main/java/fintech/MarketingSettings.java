package fintech.bo.api.model.marketing;

import lombok.Data;

@Data
public class MarketingSettings {

    private String emailFrom;
    private String emailFromName;
    private String smsSenderId;
    private String emailReplyTo;

}
