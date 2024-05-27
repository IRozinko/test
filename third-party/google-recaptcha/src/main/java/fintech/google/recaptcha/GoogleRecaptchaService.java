package fintech.google.recaptcha;

public interface GoogleRecaptchaService {

    boolean isResponseValid(VerifyCaptchaCommand command);
}
