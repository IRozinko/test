package fintech.rules.model;


public interface Rule {

    RuleResult execute(RuleContext context, RuleResultBuilder builder);

    String getName();
}
