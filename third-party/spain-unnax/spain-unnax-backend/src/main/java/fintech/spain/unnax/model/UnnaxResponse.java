package fintech.spain.unnax.model;

import lombok.Data;

@Data
public class UnnaxResponse<T> {

    private boolean error;
    private T response;
    private UnnaxErrorResponse errorResponse;

    public UnnaxResponse(T response) {
        this.response = response;
    }

    public UnnaxResponse(UnnaxErrorResponse errorResponse) {
        this.error = true;
        this.errorResponse = errorResponse;
    }
}
