package fintech.spain.alfa.product.risk.rules.basic;

import fintech.rules.RuleBean;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.settings.AlfaSettings;

@RuleBean
public class AgeRule extends AbstractBasicLendingRule {

    @Override
    public RuleResult execute(AlfaSettings.BasicRuleSettings.Check basicRuleSettings, BasicRuleParams basicRuleParams, RuleResultBuilder builder) {
        builder.addCheck(getName(), String.format("%d - %d", basicRuleSettings.getMinAge(), basicRuleSettings.getMaxAge()), basicRuleParams.getAge());

        if (basicRuleSettings.getMinAge() > basicRuleParams.getAge()) {
            return builder.reject(AlfaConstants.REJECT_REASON_AGE_TOO_YOUNG);
        }

        if (basicRuleSettings.getMaxAge() < basicRuleParams.getAge()) {
            return builder.reject(AlfaConstants.REJECT_REASON_AGE_TOO_OLD);
        }

        return builder.approve();
    }

    @Override
    public String getName() {
        return "Age";
    }
}
