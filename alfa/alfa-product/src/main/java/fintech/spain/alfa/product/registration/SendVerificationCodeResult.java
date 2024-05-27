package fintech.spain.alfa.product.registration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendVerificationCodeResult {

    private boolean codeSent;
    private int availableAttempts;
    private long nextAttemptInSeconds;

}
