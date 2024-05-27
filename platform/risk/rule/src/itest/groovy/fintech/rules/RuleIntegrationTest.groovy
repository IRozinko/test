package fintech.rules

import fintech.TimeMachine
import fintech.rules.db.RuleLogRepository
import fintech.rules.db.RuleSetLogRepository
import fintech.rules.model.Decision
import fintech.rules.model.RuleContext
import org.springframework.beans.factory.annotation.Autowired

class RuleIntegrationTest extends RuleBaseSpecification {

    @Autowired
    RuleService ruleService

    @Autowired
    RuleSetLogRepository ruleSetLogRepository

    @Autowired
    RuleLogRepository ruleLogRepository

    def "Approve"() {
        given:
        def set = ruleService.buildRuleSet("my rule set", [DummyApproveRule.class])
        def when = TimeMachine.now()

        when:
        def result = ruleService.executeAndLog(set, RuleContext.builder()
            .when(when)
            .clientId(1L)
            .loanId(2L)
            .applicationId(3L)
            .build())

        then:
        result.decision == Decision.APPROVE

        and:
        with(ruleSetLogRepository.findAll()[0]) {
            decision == Decision.APPROVE
            clientId == 1L
            loanId == 2L
            applicationId == 3L
            executedAt == when
            ruleSet == "my rule set"
        }

        and:
        with(ruleLogRepository.findAll()[0]) {
            decision == Decision.APPROVE
            rule == DummyApproveRule.RULE_NAME
            checksJson.contains("ok")
        }
    }

    def "Manual"() {
        given:
        def set = ruleService.buildRuleSet("my rule set", [DummyApproveRule.class, DummyManualRule.class])
        def when = TimeMachine.now()

        when:
        def result = ruleService.executeAndLog(set, RuleContext.builder()
            .when(when)
            .clientId(1L)
            .loanId(2L)
            .applicationId(3L)
            .build())

        then:
        result.decision == Decision.MANUAL

        and:
        with(ruleSetLogRepository.findAll()[0]) {
            decision == Decision.MANUAL
            rejectReason == DummyManualRule.REASON
        }
    }

    def "Reject"() {
        given:
        def set = ruleService.buildRuleSet("my rule set", [DummyApproveRule.class, DummyRejectRule.class, DummyManualRule.class])
        def when = TimeMachine.now()

        when:
        def result = ruleService.executeAndLog(set, RuleContext.builder()
            .when(when)
            .clientId(1L)
            .loanId(2L)
            .applicationId(3L)
            .build())

        then:
        result.decision == Decision.REJECT

        and:
        with(ruleSetLogRepository.findAll()[0]) {
            decision == Decision.REJECT
            rejectReason == DummyRejectRule.REASON
            rejectReasonDetails == DummyRejectRule.REASON_DETAIL
        }

    }
}
