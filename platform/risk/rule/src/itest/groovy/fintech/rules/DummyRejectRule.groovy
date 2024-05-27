package fintech.rules

import fintech.rules.model.Rule
import fintech.rules.model.RuleContext
import fintech.rules.model.RuleResult
import fintech.rules.model.RuleResultBuilder

@RuleBean
class DummyRejectRule implements Rule {

    public static final String REASON = "reject reason"
    public static final String REASON_DETAIL = "reject reason detail"

    @Override
    RuleResult execute(RuleContext ruleContext, RuleResultBuilder builder) {
        return builder.addCheck("reject check", 0, 1).reject(REASON, REASON_DETAIL)
    }

    @Override
    String getName() {
        return "DummyRejectRule"
    }
}
