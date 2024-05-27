package fintech.spain.web.common;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ValidationExceptions {

    @Autowired
    private ApiLocalization apiLocalization;

    public ValidationException notUnique(String field) {
        return build(field, "NotUnique");
    }

    public ValidationException invalidValue(String field) {
        return build(field, "InvalidValue");
    }

    public ValidationException invalidValue(String field, Object... arguments) {
        return build(field, "InvalidValue", arguments);
    }

    public ValidationException maxAttemptsReached(String field, Object... arguments) {
        return build(field, "MaxAttemptsReached", arguments);
    }

    public ValidationException alreadyVerified(String field) {
        return build(field, "AlreadyVerified");
    }

    public ValidationException loanAlreadyAccepted(String field) {
        return build(field, "LoanAlreadyAccepted");
    }

    public ValidationException expired(String field) {
        return build(field, "Expired");
    }

    private ValidationException build(String field, String code, Object... arguments) {
        String message = apiLocalization.localizeErrorMessage(new String[]{code + "." + field, code}, arguments, code, Locale.getDefault());
        ApiError.FieldError fieldError = new ApiError.FieldError(field, code, message);
        ApiError error = new ApiError("Bad request", ImmutableList.of(), ImmutableList.of(fieldError));
        return new ValidationException(error);
    }
}
