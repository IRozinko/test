package fintech.spain.alfa.product.validators;

import fintech.crm.country.CountryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NationalityValidator implements ConstraintValidator<Nationality, String> {

    @Autowired
    private CountryService countryService;

    @Override
    public void initialize(Nationality constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return countryService.isValid(value);
        }
        return true;
    }
}
