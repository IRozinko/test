package fintech.rules.model;


import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class RuleSetResult {

    private final String ruleSetName;
    private final Decision decision;
    private final List<RuleResult> ruleResults;

    public RuleSetResult(String ruleSetName, Decision decision, List<RuleResult> ruleResults) {
        this.ruleSetName = ruleSetName;
        this.decision = decision;
        this.ruleResults = ImmutableList.copyOf(ruleResults);
    }

    public Optional<RuleResult> getFirstRuleResult(Decision decision) {
        return ruleResults.stream().filter(ruleResult -> ruleResult.getDecision() == decision).findFirst();
    }
}
