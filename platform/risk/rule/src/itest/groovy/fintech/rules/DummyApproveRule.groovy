package fintech.rules

import fintech.rules.model.Rule
import fintech.rules.model.RuleContext
import fintech.rules.model.RuleResult
import fintech.rules.model.RuleResultBuilder

@RuleBean
class DummyApproveRule implements Rule {

    public static final String RULE_NAME = "DummyApprove"

    @Override
    RuleResult execute(RuleContext ruleContext, RuleResultBuilder builder) {
        return builder.addCheck("approve check", "ok", "ok").approve()
    }

    @Override
    String getName() {
        return RULE_NAME
    }
}
