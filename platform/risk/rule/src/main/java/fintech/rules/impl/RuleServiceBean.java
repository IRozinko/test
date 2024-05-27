package fintech.rules.impl;

import fintech.JsonUtils;
import fintech.Validate;
import fintech.rules.RuleBean;
import fintech.rules.RuleService;
import fintech.rules.db.RuleLogEntity;
import fintech.rules.db.RuleLogRepository;
import fintech.rules.db.RuleSetLogEntity;
import fintech.rules.db.RuleSetLogRepository;
import fintech.rules.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class RuleServiceBean implements RuleService {

    @Autowired
    private RuleLogRepository ruleLogRepository;

    @Autowired
    private RuleSetLogRepository ruleSetLogRepository;

    @Autowired
    private ApplicationContext applicationContext;

    private static void assertIsAnnotatedAsRuleBean(Class<?> componentClass) {
        Validate.notNull(AnnotationUtils.findAnnotation(componentClass, RuleBean.class), "Class %s is not annotated with @RuleBean", componentClass);
    }

    @Override
    public RuleSetResult executeAndLog(RuleSet ruleSet, RuleContext context) {
        RuleSetResult result = ruleSet.execute(context);
        RuleSetLogEntity entity = new RuleSetLogEntity();
        entity.setLoanId(context.getLoanId());
        entity.setClientId(context.getClientId());
        entity.setApplicationId(context.getApplicationId());
        entity.setExecutedAt(context.getWhen());
        entity.setDecision(result.getDecision());
        entity.setRuleSet(result.getRuleSetName());

        result.getFirstRuleResult(Decision.MANUAL).ifPresent(ruleResult -> {
            entity.setRejectReason(ruleResult.getReason());
            entity.setRejectReasonDetails(ruleResult.getReasonDetails());
        });
        result.getFirstRuleResult(Decision.REJECT).ifPresent(ruleResult -> {
            entity.setRejectReason(ruleResult.getReason());
            entity.setRejectReasonDetails(ruleResult.getReasonDetails());
        });
        result.getFirstRuleResult(Decision.CANCEL).ifPresent(ruleResult -> {
            entity.setRejectReason(ruleResult.getReason());
            entity.setRejectReasonDetails(ruleResult.getReasonDetails());
        });

        ruleSetLogRepository.saveAndFlush(entity);
        for (RuleResult ruleResult : result.getRuleResults()) {
            saveRuleResults(entity, ruleResult);
        }
        return result;
    }

    private void saveRuleResults(RuleSetLogEntity ruleSetLog, RuleResult ruleResult) {
        RuleLogEntity entity = new RuleLogEntity();
        entity.setRuleSetResult(ruleSetLog);
        entity.setLoanId(ruleSetLog.getLoanId());
        entity.setClientId(ruleSetLog.getClientId());
        entity.setApplicationId(ruleSetLog.getApplicationId());
        entity.setChecksJson(JsonUtils.writeValueAsString(ruleResult.getChecks()));
        entity.setDecision(ruleResult.getDecision());
        entity.setReason(ruleResult.getReason());
        entity.setReasonDetails(ruleResult.getReasonDetails());
        entity.setRule(ruleResult.getRuleName());
        ruleLogRepository.saveAndFlush(entity);
    }

    @Override
    public RuleSet buildRuleSet(String ruleSetName, List<Class<? extends Rule>> ruleBeanClasses) {
        Validate.notBlank(ruleSetName, "Empty rule set ruleSetName");
        Validate.notEmpty(ruleBeanClasses, "Empty rule bean classes");
        List<Rule> rules = new ArrayList<>();
        for (Class<? extends Rule> ruleClass : ruleBeanClasses) {
            assertIsAnnotatedAsRuleBean(ruleClass);
            Rule rule = applicationContext.getBean(ruleClass);
            rules.add(rule);
        }
        return new RuleSet(ruleSetName, rules);
    }
}
