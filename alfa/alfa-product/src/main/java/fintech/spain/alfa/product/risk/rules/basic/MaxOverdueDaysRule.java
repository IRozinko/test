package fintech.spain.alfa.product.risk.rules.basic;

import fintech.rules.RuleBean;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.settings.AlfaSettings;

@RuleBean
public class MaxOverdueDaysRule extends AbstractBasicLendingRule {

    @Override
    public RuleResult execute(AlfaSettings.BasicRuleSettings.Check basicRuleSettings, BasicRuleParams basicRuleParams, RuleResultBuilder builder) {
        builder.addCheck(getName(), String.format("<= %d", basicRuleSettings.getMaxOverdueDays()), basicRuleParams.getMaxOverdueDays());

        if (basicRuleSettings.getMaxOverdueDays() < basicRuleParams.getMaxOverdueDays()) {
            return builder.reject(getName());
        }

        return builder.approve();
    }

    @Override
    public String getName() {
        return "MaxOverdueDays";
    }
}
