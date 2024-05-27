package fintech.spain.web.common;

public class ValidationException extends RuntimeException {

    private final ApiError error;

    public ValidationException(ApiError error) {
        this.error = error;
    }

    public ApiError getError() {
        return error;
    }


}
