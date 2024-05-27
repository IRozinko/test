package fintech.spain.alfa.product.validators;

import fintech.crm.CrmConstants;
import fintech.crm.documents.IdentityDocumentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueDocumentNumberValidator implements ConstraintValidator<UniqueDocumentNumber, String> {

    @Autowired
    private IdentityDocumentService identityDocumentService;

    @Override
    public void initialize(UniqueDocumentNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.isBlank(value)) {
            return identityDocumentService.isDocumentNumberAvailable(value, CrmConstants.IDENTITY_DOCUMENT_DNI);
        }
        return true;
    }
}
