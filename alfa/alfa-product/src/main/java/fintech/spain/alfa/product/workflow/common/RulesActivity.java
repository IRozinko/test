package fintech.spain.alfa.product.workflow.common;

import fintech.TimeMachine;
import fintech.rules.RuleService;
import fintech.rules.model.Decision;
import fintech.rules.model.Rule;
import fintech.rules.model.RuleContext;
import fintech.rules.model.RuleResult;
import fintech.rules.model.RuleSet;
import fintech.rules.model.RuleSetResult;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class RulesActivity implements ActivityHandler {

    @Autowired
    private RuleService ruleService;

    private final String ruleSetName;
    private final List<Class<? extends Rule>> rules;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RulesActivity(String ruleSetName, List<Class<? extends Rule>> rules) {
        this.ruleSetName = ruleSetName;
        this.rules = rules;
    }

    @Override
    public ActivityResult handle(ActivityContext context) {
        Long clientId = context.getWorkflow().getClientId();
        Long applicationId = context.getWorkflow().getApplicationId();
        Long loanId = context.getWorkflow().getLoanId();

        RuleSet ruleSet = ruleService.buildRuleSet(ruleSetName, rules);
        RuleSetResult result = ruleService.executeAndLog(ruleSet, RuleContext.builder()
            .applicationId(applicationId)
            .clientId(clientId)
            .loanId(loanId)
            .when(TimeMachine.now())
            .attributes(context.getWorkflow().getAttributes())
            .workflowName(context.getWorkflow().getName())
            .build()
        );

        if (this.checkMaxAttemptsExceeded(context)) {
            return ActivityResult
                .resolution(Resolutions.REJECT, "Max attempts exceeded with resolution: " + result
                    .getFirstRuleResult(Decision.REJECT)
                    .map(RuleResult::getReason)
                .orElse("empty resolution"));
        } else if (result.getDecision() == Decision.APPROVE) {
            return ActivityResult.resolution(Resolutions.APPROVE, "");
        } else if (result.getDecision() == Decision.MANUAL) {
            return ActivityResult.resolution(Resolutions.MANUAL, "");
        } else if (result.getDecision() == Decision.REJECT) {
            return ActivityResult.resolution(Resolutions.REJECT, result.getFirstRuleResult(Decision.REJECT).map(RuleResult::getReason).orElse(""));
        } else {
            return ActivityResult.resolution(Resolutions.CANCEL, result.getFirstRuleResult(Decision.CANCEL).map(RuleResult::getReason).orElse(""));
        }
    }
}
