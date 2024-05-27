package fintech.spain.alfa.bo.api;

import fintech.bo.api.server.BackofficeApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice(basePackages = "fintech.spain.alfa.bo.api")
@ResponseBody
public class AlfaBackofficeApiExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BackofficeApiError methodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<BackofficeApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream().map(error -> new BackofficeApiError.FieldError(error.getField(), error.getCode(), error.getDefaultMessage())).collect(Collectors.toList());
        List<BackofficeApiError.GlobalError> globalErrors = ex.getBindingResult().getGlobalErrors().stream().map(error -> new BackofficeApiError.GlobalError(error.getCode(), error.getDefaultMessage())).collect(Collectors.toList());
        BackofficeApiError response = new BackofficeApiError("Bad request", globalErrors, fieldErrors);
        log.info("Bad request, {}", response);
        return response;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public BackofficeApiError accessDeniedException(AccessDeniedException ex) {
        log.info("Access denied: {}", ex.getMessage());
        return new BackofficeApiError("Access denied");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public BackofficeApiError exception(Exception ex) {
        log.error("Internal error", ex);
        BackofficeApiError response = new BackofficeApiError(String.format("Error: %s", ex.getMessage()));
        return response;
    }
}
