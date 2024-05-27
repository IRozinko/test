package fintech.google.recaptcha;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyCaptchaCommand {
    private String recaptchaResponse;
    private String ipAddress;
}
