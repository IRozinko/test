package fintech.bo.api.server;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class BackofficeApiError {

    private String message;
    private List<GlobalError> globalErrors;
    private Map<String, FieldError> fieldErrors = new HashMap<>();

    public BackofficeApiError(String message) {
        this.message = message;
    }

    public BackofficeApiError(String message, List<GlobalError> globalErrors) {
        this.message = message;
        this.globalErrors = globalErrors;
    }

    public BackofficeApiError(String message, List<GlobalError> globalErrors, List<FieldError> fieldErrors) {
        this.message = message;
        this.globalErrors = globalErrors;
        fieldErrors.stream().forEach(e -> this.fieldErrors.put(e.getField(), e));
    }

    @Data
    public static class GlobalError {
        private String code;
        private String message;

        public GlobalError(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    @Data
    public static class FieldError {
        private String field;
        private String code;
        private String message;

        public FieldError(String field, String code, String message) {
            this.field = field;
            this.code = code;
            this.message = message;
        }
    }
}
