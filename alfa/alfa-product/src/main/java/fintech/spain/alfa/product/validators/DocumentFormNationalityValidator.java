package fintech.spain.alfa.product.validators;

import fintech.crm.country.Country;
import fintech.crm.country.CountryService;
import fintech.crm.documents.IdentityDocumentNumberUtils;
import fintech.spain.alfa.product.registration.forms.DocumentNumberForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DocumentFormNationalityValidator implements ConstraintValidator<ValidDocumentFormNationality, DocumentNumberForm> {

    @Autowired
    private CountryService countryService;

    @Override
    public void initialize(ValidDocumentFormNationality constraintAnnotation) {

    }

    @Override
    public boolean isValid(DocumentNumberForm form, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(form.getDocumentNumber()) && countryService.isValid(form.getCountryCodeOfNationality())) {
            Country country = countryService.getCountry(form.getCountryCodeOfNationality());
            if (IdentityDocumentNumberUtils.isValidDni(form.getDocumentNumber())) {
                return country.isHomeCountry();
            }
            if (IdentityDocumentNumberUtils.isValidNie(form.getDocumentNumber())) {
                return !country.isHomeCountry();
            }
        }

        return true;
    }
}
