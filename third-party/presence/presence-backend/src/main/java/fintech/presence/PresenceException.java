package fintech.presence;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class PresenceException extends Exception {

    private int errorCode;
    private String errorMessage;

    public PresenceException(String message) {
        super(message);
    }

    public PresenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PresenceException(String message, int errorCode, String errorMessage) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
