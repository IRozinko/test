package fintech.crm.contacts;

public class PhoneVerificationCodeExpiredException extends RuntimeException {

    public PhoneVerificationCodeExpiredException(String message) {
        super(message);
    }
}
