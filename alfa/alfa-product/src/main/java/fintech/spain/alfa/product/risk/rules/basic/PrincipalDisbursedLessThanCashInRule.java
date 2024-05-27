package fintech.spain.alfa.product.risk.rules.basic;

import fintech.BigDecimalUtils;
import fintech.rules.RuleBean;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.settings.AlfaSettings;

@RuleBean
public class PrincipalDisbursedLessThanCashInRule extends AbstractBasicLendingRule {

    @Override
    public RuleResult execute(AlfaSettings.BasicRuleSettings.Check basicRuleSettings, BasicRuleParams basicRuleParams, RuleResultBuilder builder) {
        builder.addCheck(getName(), String.format("<= %f", basicRuleParams.getCashIn()), basicRuleParams.getPrincipalDisbursed());

        if (BigDecimalUtils.lt(basicRuleParams.getCashIn(), basicRuleParams.getPrincipalDisbursed())) {
            return builder.reject(getName());
        }

        return builder.approve();
    }

    @Override
    public String getName() {
        return "PrincipalDisbursedLessThanCashIn";
    }
}
