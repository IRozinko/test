package fintech.spain.alfa.product.affiliate.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AcceptTermsValidator implements ConstraintValidator<AcceptTerms, Integer> {

    @Override
    public void initialize(AcceptTerms constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && value == 1;
    }
}
