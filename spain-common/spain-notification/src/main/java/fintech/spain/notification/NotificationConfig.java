package fintech.spain.notification;

import lombok.Data;

@Data
public class NotificationConfig {

    private String emailFrom;
    private String emailFromName;
    private String smsSenderId;
    private String emailReplyTo;

}
