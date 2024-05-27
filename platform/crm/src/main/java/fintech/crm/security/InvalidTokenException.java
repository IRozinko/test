package fintech.crm.security;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String s) {
        super(s);
    }
}
