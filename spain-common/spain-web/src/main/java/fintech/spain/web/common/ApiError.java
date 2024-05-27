package fintech.spain.web.common;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class ApiError {

    private String message;
    private List<GlobalError> globalErrors;
    private Map<String, FieldError> fieldErrors = new HashMap<>();

    private ApiError() {
    }

    public ApiError(String message) {
        this.message = message;
    }

    public ApiError(String message, List<GlobalError> globalErrors) {
        this.message = message;
        this.globalErrors = globalErrors;
    }

    public ApiError(String message, List<GlobalError> globalErrors, List<FieldError> fieldErrors) {
        this.message = message;
        this.globalErrors = globalErrors;
        fieldErrors.stream().forEach(e -> this.fieldErrors.put(e.getField(), e));
    }

    @Data
    public static class GlobalError {
        private String code;
        private String message;

        private GlobalError() {
        }

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

        private FieldError() {
        }

        public FieldError(String field, String code, String message) {
            this.field = field;
            this.code = code;
            this.message = message;
        }
    }
}
