package fintech.notification;

import fintech.email.Email;
import fintech.sms.Sms;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notification {

    private Email email;
    private Sms sms;

    private String cmsKey;

    private LocalDateTime sentAt;

    private Long clientId;
    private Long loanId;
    private Long loanApplicationId;
    private Long debtId;
    private Long taskId;
}
