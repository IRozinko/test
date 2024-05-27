package fintech.spain.platform.web.validations;

import fintech.crm.contacts.PhoneContactService;
import fintech.crm.contacts.PhoneNumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniquePhoneNumberValidator implements ConstraintValidator<UniquePhoneNumber, String> {

    @Autowired
    private PhoneContactService phoneContactService;

    @Override
    public void initialize(UniquePhoneNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        return phoneContactService.isPrimaryPhoneNumberAvailable(PhoneNumberUtils.normalize(value));
    }
}
