package fintech.rules.model;


import com.google.common.collect.ImmutableList;
import fintech.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleSet {

    private final String name;
    private final List<Rule> rules;

    public RuleSet(String name, List<Rule> rules) {
        this.name = name;
        this.rules = ImmutableList.copyOf(rules);
    }

    public RuleSet(String name, Rule... rules) {
        this.name = name;
        this.rules = ImmutableList.copyOf(rules);
    }

    public RuleSetResult execute(RuleContext context) {
        List<RuleResult> results = new ArrayList<>();
        for (Rule rule : rules) {
            String name = rule.getName();
            RuleResult result = rule.execute(context, new RuleResultBuilder(name));
            Validate.isTrue(result != null, "Result of a rule can not be 'null'!");
            results.add(result);
        }
        Set<Decision> decisions = results.stream().map(RuleResult::getDecision).collect(Collectors.toSet());
        Decision decision = Decision.APPROVE;
        if (decisions.contains(Decision.MANUAL)) {
            decision = Decision.MANUAL;
        }
        if (decisions.contains(Decision.REJECT)) {
            decision = Decision.REJECT;
        }
        if (decisions.contains(Decision.CANCEL)) {
            decision = Decision.CANCEL;
        }
        return new RuleSetResult(this.name, decision, results);
    }
}
