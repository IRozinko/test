package fintech.spain.alfa.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendVerificationCodeOkResult {

    private String result = "OK";
    private boolean codeSent;
    private int availableAttempts;
    private long nextAttemptInSeconds;

    public static SendVerificationCodeOkResult OK(boolean codeSent, int availableAttempts, long nextAttemptInSeconds) {
        SendVerificationCodeOkResult result = new SendVerificationCodeOkResult();
        result.codeSent = codeSent;
        result.availableAttempts = availableAttempts;
        result.nextAttemptInSeconds = nextAttemptInSeconds;
        return result;
    }
}
