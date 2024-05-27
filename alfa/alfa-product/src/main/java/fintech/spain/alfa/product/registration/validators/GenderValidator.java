package fintech.spain.alfa.product.registration.validators;

import fintech.crm.client.Gender;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<ValidGender, String> {

    @Override
    public void initialize(ValidGender constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return Gender.MALE.name().equals(value) || Gender.FEMALE.name().equals(value);
    }
}
