package fintech.spain.alfa.product.risk.rules.basic;

import fintech.rules.RuleBean;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleResultBuilder;
import fintech.spain.alfa.product.settings.AlfaSettings;

@RuleBean
public class DaysSinceLastApplicationRejectAndRejectionReasonRule extends AbstractBasicLendingRule {

    @Override
    public RuleResult execute(AlfaSettings.BasicRuleSettings.Check basicRuleSettings, BasicRuleParams basicRuleParams, RuleResultBuilder builder) {
        if (basicRuleParams.getDaysSinceLastApplicationRejection() == null) {
            return builder.approve();
        }

        builder.addCheck(getName(), String.format("in %s and < %d", basicRuleSettings.getLastLoanApplicationRejectionReason(), basicRuleSettings.getDaysSinceLastApplicationRejection()), String.format("%s and %d", basicRuleParams.getLastLoanApplicationRejectionReason(), basicRuleParams.getDaysSinceLastApplicationRejection()));

        if (basicRuleSettings.getDaysSinceLastApplicationRejection() > basicRuleParams.getDaysSinceLastApplicationRejection()
            && basicRuleSettings.getLastLoanApplicationRejectionReason().contains(basicRuleParams.getLastLoanApplicationRejectionReason())) {
            return builder.reject(getName());
        }

        return builder.approve();
    }

    @Override
    public String getName() {
        return "DaysSinceLastApplicationRejectAndRejectionReason";
    }
}
