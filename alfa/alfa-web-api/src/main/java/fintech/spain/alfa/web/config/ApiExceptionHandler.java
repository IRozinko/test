package fintech.spain.alfa.web.config;

import fintech.spain.alfa.web.common.NotFoundException;
import fintech.spain.web.common.ApiError;
import fintech.spain.web.common.ApiLocalization;
import fintech.spain.web.common.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice(basePackages = "fintech.spain")
@ResponseBody
public class ApiExceptionHandler {

    @Autowired
    private ApiLocalization apiLocalization;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError methodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream().map(error -> mapFieldError(error, locale)).collect(Collectors.toList());
        List<ApiError.GlobalError> globalErrors = ex.getBindingResult().getGlobalErrors().stream().map(error -> mapGlobalError(error, locale)).collect(Collectors.toList());
        ApiError response = new ApiError("Bad request", globalErrors, fieldErrors);
        log.info("Bad request, {}", response);
        return response;
    }

    private ApiError.GlobalError mapGlobalError(ObjectError error, Locale locale) {
        String message = localizeErrorMessage(error, locale);
        return new ApiError.GlobalError(error.getCode(), message);
    }

    private ApiError.FieldError mapFieldError(FieldError error, Locale locale) {
        String message = localizeErrorMessage(error, locale);
        return new ApiError.FieldError(error.getField(), error.getCode(), message);
    }

    private String localizeErrorMessage(ObjectError error, Locale locale) {
        return apiLocalization.localizeErrorMessage(error, locale);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ApiError validationException(ValidationException ex) {
        ApiError error = ex.getError();
        log.info("Validation error, {}", error);
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiError illegalArgumentException(IllegalArgumentException ex) {
        log.info("Bad request", ex);
        return new ApiError("Bad request");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ApiError accessDeniedException(AccessDeniedException ex) {
        log.info("Access denied: {}", ex.getMessage());
        return new ApiError("Access denied");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError exception(Exception ex) {
        log.error("Internal error", ex);
        return new ApiError("Internal error: " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ApiError notFound(NotFoundException ex) {
        log.info("Not found: {}", ex.getMessage());
        return new ApiError("Not found");
    }
}
