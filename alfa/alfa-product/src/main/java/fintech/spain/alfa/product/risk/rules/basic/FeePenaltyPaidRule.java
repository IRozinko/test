package fintech.spain.alfa.product.risk.rules.basic;

import fintech.BigDecimalUtils;
import fintech.rules.RuleBean;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.settings.AlfaSettings;

@RuleBean
public class FeePenaltyPaidRule extends AbstractBasicLendingRule {

    @Override
    public RuleResult execute(AlfaSettings.BasicRuleSettings.Check basicRuleSettings, BasicRuleParams basicRuleParams, RuleResultBuilder builder) {
        builder.addCheck(getName(), String.format("<= %f", basicRuleSettings.getFeePenaltyPaid()), basicRuleParams.getFeePaid().add(basicRuleParams.getPenaltyPaid()));

        if (BigDecimalUtils.lt(basicRuleSettings.getFeePenaltyPaid(), basicRuleParams.getFeePaid().add(basicRuleParams.getPenaltyPaid()))) {
            return builder.reject(getName());
        }

        return builder.approve();
    }

    @Override
    public String getName() {
        return "FeePenaltyPaid";
    }
}
