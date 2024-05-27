package fintech.rules;

import fintech.rules.model.Rule;
import fintech.rules.model.RuleContext;
import fintech.rules.model.RuleSet;
import fintech.rules.model.RuleSetResult;

import java.util.List;

public interface RuleService {

    RuleSetResult executeAndLog(RuleSet ruleSet, RuleContext context);

    RuleSet buildRuleSet(String name, List<Class<? extends Rule>> ruleBeanClasses);
}
