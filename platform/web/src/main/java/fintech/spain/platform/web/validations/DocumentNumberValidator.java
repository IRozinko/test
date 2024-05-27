package fintech.spain.platform.web.validations;

import fintech.crm.documents.IdentityDocumentNumberUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DocumentNumberValidator implements ConstraintValidator<DocumentNumber, String> {

    @Override
    public void initialize(DocumentNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !StringUtils.isBlank(value) && IdentityDocumentNumberUtils.isValidDniOrNie(value);
    }
}
