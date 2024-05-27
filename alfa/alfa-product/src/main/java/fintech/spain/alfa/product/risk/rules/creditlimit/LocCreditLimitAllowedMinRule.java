package fintech.spain.alfa.product.risk.rules.creditlimit;

import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.rules.RuleBean;
import fintech.rules.model.Rule;
import fintech.rules.model.RuleContext;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.settings.LocSettings;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.BigDecimalUtils.lt;

@RuleBean
public class LocCreditLimitAllowedMinRule implements Rule {

    @Autowired
    private SettingsService settingsService;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public RuleResult execute(RuleContext context, RuleResultBuilder builder) {
        LocSettings.LocCreditLimitSettings settings = settingsService.getJson(LocSettings.LOC_CREDIT_LIMIT_SETTINGS, LocSettings.LocCreditLimitSettings.class);
        LoanApplication loanApplication = loanApplicationService.get(context.getApplicationId());
        builder.addCheck("MinCreditLimitAllowed", settings.getMinCreditLimitAllowed(), loanApplication.getCreditLimit());
        if (lt(loanApplication.getCreditLimit(), settings.getMinCreditLimitAllowed())) {
            return builder.reject(AlfaConstants.REJECT_REASON_AMOUNT_BELOW_MINIMUM);
        } else {
            return builder.approve();
        }
    }

    @Override
    public String getName() {
        return "LocCreditLimitAllowedMin";
    }
}
