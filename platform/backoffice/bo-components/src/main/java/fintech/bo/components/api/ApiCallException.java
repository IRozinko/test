package fintech.bo.components.api;

public class ApiCallException extends RuntimeException {

    private String body;

    public ApiCallException(String message, String body) {
        super(message);
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
