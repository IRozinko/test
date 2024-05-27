package fintech.spain.alfa.web.validators;

import fintech.lending.core.product.ProductService;
import fintech.lending.payday.settings.PaydayOfferSettings;
import fintech.lending.payday.settings.PaydayProductSettings;
import fintech.spain.alfa.product.AlfaConstants;
import org.apache.commons.lang.math.LongRange;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoanTermValidator implements ConstraintValidator<LoanTerm, Long> {

    @Autowired
    private ProductService productService;

    @Override
    public void initialize(LoanTerm loanTerm) {
    }

    @Override
    public boolean isValid(Long terms, ConstraintValidatorContext context) {
        if (terms != null) {
            PaydayProductSettings settings = productService.getSettings(AlfaConstants.PRODUCT_ID, PaydayProductSettings.class);
            PaydayOfferSettings offerSettings = settings.getPublicOfferSettings();
            return new LongRange(offerSettings.getMinTerm(), offerSettings.getMaxTerm()).containsLong(terms);
        } else {
            return true;
        }
    }

}
