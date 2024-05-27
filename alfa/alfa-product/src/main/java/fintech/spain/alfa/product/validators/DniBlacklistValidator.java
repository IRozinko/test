package fintech.spain.alfa.product.validators;

import fintech.risk.checklist.CheckListConstants;
import fintech.risk.checklist.CheckListService;
import fintech.risk.checklist.model.CheckListQuery;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DniBlacklistValidator implements ConstraintValidator<DniBlacklist, String> {

    @Autowired
    private CheckListService checkListService;

    @Override
    public void initialize(DniBlacklist constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        return checkListService.isAllowed(new CheckListQuery(CheckListConstants.CHECKLIST_TYPE_DNI, value));
    }
}
