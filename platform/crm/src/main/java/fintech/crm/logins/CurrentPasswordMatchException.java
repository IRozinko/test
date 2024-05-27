package fintech.crm.logins;

public class CurrentPasswordMatchException extends RuntimeException {
    public CurrentPasswordMatchException(String message) {
        super(message);
    }
}
