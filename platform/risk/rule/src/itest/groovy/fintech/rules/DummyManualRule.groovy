package fintech.rules

import fintech.rules.model.Rule
import fintech.rules.model.RuleContext
import fintech.rules.model.RuleResult
import fintech.rules.model.RuleResultBuilder

@RuleBean
class DummyManualRule implements Rule {

    public static final String REASON = "manual reason"

    @Override
    RuleResult execute(RuleContext ruleContext, RuleResultBuilder builder) {
        return builder.addCheck("manual check", 0, 1).manual(REASON)
    }

    @Override
    String getName() {
        return "DummyManualRule"
    }
}
