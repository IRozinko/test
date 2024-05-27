package fintech.rules.model;

import com.google.common.collect.ImmutableList;
import fintech.Validate;

import java.util.ArrayList;
import java.util.List;

public class RuleResultBuilder {

    private final List<RuleResult.Check> checks = new ArrayList<>();
    private final String ruleName;

    public RuleResultBuilder(String ruleName) {
        this.ruleName = ruleName;
    }

    public RuleResultBuilder addCheck(String name, Object expectedValue, Object evaluatedValue) {
        this.checks.add(new RuleResult.Check(name, expectedValue, evaluatedValue));
        return this;
    }

    public RuleResult approve() {
        return approve("");
    }

    public RuleResult approve(String reason) {
        return new RuleResult(ruleName, Decision.APPROVE, reason, "", ImmutableList.copyOf(checks));
    }

    public RuleResult reject(String reason, String reasonDetails) {
        return new RuleResult(ruleName, Decision.REJECT, Validate.notBlank(reason), reasonDetails, ImmutableList.copyOf(checks));
    }

    public RuleResult reject(String reason) {
        return reject(reason, "");
    }

    public RuleResult cancel(String reason, String reasonDetails) {
        return new RuleResult(ruleName, Decision.CANCEL, Validate.notBlank(reason), reasonDetails, ImmutableList.copyOf(checks));
    }

    public RuleResult cancel(String reason) {
        return cancel(reason, "");
    }

    public RuleResult manual(String reason) {
        return new RuleResult(ruleName, Decision.MANUAL, Validate.notBlank(reason), "", ImmutableList.copyOf(checks));
    }
}
