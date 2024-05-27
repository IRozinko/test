package fintech.spain.alfa.product.risk.rules.basic;

import fintech.JsonUtils;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.rules.model.Rule;
import fintech.rules.model.RuleContext;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.spain.alfa.product.workflow.common.CollectAndSaveBasicInformation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public abstract class AbstractBasicLendingRule implements Rule {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private LoanService loanService;

    @Override
    public RuleResult execute(RuleContext context, RuleResultBuilder builder) {
        Map<String, String> params = loanApplicationService.getParams(context.getApplicationId());

        if (!params.containsKey(CollectAndSaveBasicInformation.BASIC_INFORMATION_ATTRIBUTE)) {
            return builder.reject("Unable to find BasicInformation in application params");
        }

        AlfaSettings.BasicRuleSettings settings = settingsService.getJson(AlfaSettings.LENDING_RULES_BASIC, AlfaSettings.BasicRuleSettings.class);
        boolean repeatedClient = !loanService.findLoans(LoanQuery.nonVoidedLoans(context.getClientId())).isEmpty();
        AlfaSettings.BasicRuleSettings.Check checkSettings = repeatedClient ? settings.getRepeatedClientCheck() : settings.getNewClientCheck();
        BasicRuleParams basicRuleParams = JsonUtils.readValue(params.get(CollectAndSaveBasicInformation.BASIC_INFORMATION_ATTRIBUTE), BasicRuleParams.class);

        return execute(checkSettings, basicRuleParams, builder);
    }

    public abstract RuleResult execute(AlfaSettings.BasicRuleSettings.Check basicRuleSettings, BasicRuleParams basicRuleParams, RuleResultBuilder builder);
}
