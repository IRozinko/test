package fintech.payments.model;

public class StatementFormatException extends RuntimeException {

    public StatementFormatException() {
    }

    public StatementFormatException(String message) {
        super(message);
    }

    public StatementFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
