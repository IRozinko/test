package fintech.sms;

import fintech.TimeMachine;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString(of = {"to", "senderId"})
public class Sms {

    private String senderId;
    private String to;
    private String text;
    private LocalDateTime sendAt = TimeMachine.now();
    private int maxSendingAttempts = 3;
    private int attemptTimeoutInSeconds = 60;
}
