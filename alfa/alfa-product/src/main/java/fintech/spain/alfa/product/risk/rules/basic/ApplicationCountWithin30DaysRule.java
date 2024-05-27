package fintech.spain.alfa.product.risk.rules.basic;

import fintech.rules.RuleBean;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.settings.AlfaSettings;

@RuleBean
public class ApplicationCountWithin30DaysRule extends AbstractBasicLendingRule {

    @Override
    public RuleResult execute(AlfaSettings.BasicRuleSettings.Check basicRuleSettings, BasicRuleParams basicRuleParams, RuleResultBuilder builder) {
        int expectedApplicationCountWithin30Days = basicRuleParams.getPaidLoanCount() == 0
            ? basicRuleSettings.getApplicationCountWithin30DaysFirstLoan()
            : basicRuleSettings.getApplicationCountWithin30DaysRepeatedLoan();
        int actualApplicationCountWithin30Days = basicRuleParams.getApplicationCountWithin30Days();

        builder.addCheck(getName(), String.format("<= %d", expectedApplicationCountWithin30Days), actualApplicationCountWithin30Days);

        if (expectedApplicationCountWithin30Days < actualApplicationCountWithin30Days) {
            return builder.reject(getName());
        }

        return builder.approve();
    }

    @Override
    public String getName() {
        return "ApplicationCountWithin30Days";
    }
}
