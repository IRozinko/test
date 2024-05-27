package fintech.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, String> {

    private Set<String> values;

    @Override
    public void initialize(AllowedValues constraintAnnotation) {
        values = new HashSet<>(Arrays.asList(constraintAnnotation.value()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return values.contains(value);
    }

}
