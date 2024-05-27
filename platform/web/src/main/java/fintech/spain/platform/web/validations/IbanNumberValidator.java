package fintech.spain.platform.web.validations;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static fintech.IbanUtils.isIbanValid;

public class IbanNumberValidator implements ConstraintValidator<IbanNumber, String> {

    @Override
    public void initialize(IbanNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !StringUtils.isBlank(value) && isValid(value);
    }

    public static boolean isValid(String iban) {
        return isIbanValid(iban);
    }
}
