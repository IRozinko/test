package fintech.spain.alfa.product.risk.rules.basic;

import fintech.rules.RuleBean;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.settings.AlfaSettings;

@RuleBean
public class LastLoanOverdueDaysRule extends AbstractBasicLendingRule {

    @Override
    public RuleResult execute(AlfaSettings.BasicRuleSettings.Check basicRuleSettings, BasicRuleParams basicRuleParams, RuleResultBuilder builder) {
        builder.addCheck(getName(), String.format("<= %d", basicRuleSettings.getLastLoanOverdueDays()), basicRuleParams.getLastLoanOverdueDays());

        if (basicRuleSettings.getLastLoanOverdueDays() < basicRuleParams.getLastLoanOverdueDays()) {
            return builder.reject(getName());
        }

        return builder.approve();
    }

    @Override
    public String getName() {
        return "LastLoanOverdueDays";
    }
}
