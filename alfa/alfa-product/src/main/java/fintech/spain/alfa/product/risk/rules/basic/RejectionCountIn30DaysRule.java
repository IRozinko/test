package fintech.spain.alfa.product.risk.rules.basic;

import fintech.rules.RuleBean;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.settings.AlfaSettings;

@RuleBean
public class RejectionCountIn30DaysRule extends AbstractBasicLendingRule {

    @Override
    public RuleResult execute(AlfaSettings.BasicRuleSettings.Check basicRuleSettings, BasicRuleParams basicRuleParams, RuleResultBuilder builder) {
        builder.addCheck(getName(), String.format("<= %d", basicRuleSettings.getRejectionCountIn30Days()), basicRuleParams.getRejectionCountIn30Days());

        if (basicRuleSettings.getRejectionCountIn30Days() < basicRuleParams.getRejectionCountIn30Days()) {
            return builder.reject(getName());
        }

        return builder.approve();
    }

    @Override
    public String getName() {
        return "RejectionCountIn30Days";
    }
}
