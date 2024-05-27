package fintech.spain.alfa.product.dc.spi;

import fintech.dc.DcSettingsService;
import fintech.dc.model.DcSettings;
import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExtensionExceededCondition implements ConditionHandler {

    @Autowired
    private LoanService loanService;

    @Autowired
    private DcSettingsService dcSettingsService;

    @Override
    public boolean apply(ConditionContext context) {
        DcSettings.ExtensionSettings extensionSettings = dcSettingsService.getSettings().getExtensionSettings();
        Loan loan = loanService.getLoan(context.getDebt().getLoanId());
        Long extensionDays = loan.getExtendedByDays();
        return extensionDays >= extensionSettings.getMaxPeriodDays();
    }
}
