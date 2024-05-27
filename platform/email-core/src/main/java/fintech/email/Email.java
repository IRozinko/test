package fintech.email;

import fintech.TimeMachine;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(of = {"to", "from", "fromName"})
public class Email {

    private String from;
    private String fromName;
    private String to;
    private String subject;
    private String body;
    private String replyTo;
    private LocalDateTime sendAt = TimeMachine.now();
    private int maxSendingAttempts = 3;
    private int attemptTimeoutInSeconds = 60;
    private List<Long> attachmentFileIds = new ArrayList<>();
}
