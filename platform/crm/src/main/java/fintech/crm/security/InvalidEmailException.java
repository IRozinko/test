package fintech.crm.security;

public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String s) {
        super(s);
    }
}
