package fintech.spain.alfa.product.risk.rules.basic;

import fintech.rules.RuleBean;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.settings.AlfaSettings;

@RuleBean
public class MaxOverdueDaysIn12MonthsRule extends AbstractBasicLendingRule {

    @Override
    public RuleResult execute(AlfaSettings.BasicRuleSettings.Check basicRuleSettings, BasicRuleParams basicRuleParams, RuleResultBuilder builder) {
        builder.addCheck(getName(), String.format("<= %d", basicRuleSettings.getMaxOverdueDaysInLast12Months()), basicRuleParams.getMaxOverdueDaysInLast12Months());

        if (basicRuleSettings.getMaxOverdueDaysInLast12Months() < basicRuleParams.getMaxOverdueDaysInLast12Months()) {
            return builder.reject(getName());
        }

        return builder.approve();
    }

    @Override
    public String getName() {
        return "MaxOverdueDaysIn12Months";
    }
}
