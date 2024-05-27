package fintech.spain.alfa.product.affiliate.validators;

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
        return StringUtils.lowerCase(Gender.MALE.name()).equals(value) || StringUtils.lowerCase(Gender.FEMALE.name()).equals(value);
    }
}
