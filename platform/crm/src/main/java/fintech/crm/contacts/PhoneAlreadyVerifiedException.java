package fintech.crm.contacts;

public class PhoneAlreadyVerifiedException extends RuntimeException {

    public PhoneAlreadyVerifiedException(String message) {
        super(message);
    }
}
