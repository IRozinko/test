package fintech.spain.alfa.product.workflow.undewrtiting.handlers

import fintech.decision.model.DecisionRequestStatus
import fintech.decision.model.DecisionResult
import fintech.decision.spi.MockDecisionEngine
import fintech.lending.core.application.LoanApplicationService
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.strategy.CalculationStrategyService
import fintech.strategy.SaveCalculationStrategyCommand
import fintech.workflow.WorkflowStatus
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Stepwise
import spock.lang.Unroll

import static fintech.decision.spi.DecisionEngineStrategy.STRATEGY_SCENARIO
import static fintech.lending.core.application.LoanApplicationStatusDetail.PENDING
import static fintech.spain.alfa.product.workflow.common.Resolutions.OK
import static fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.ASSIGN_STRATEGIES
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.DOWJONES
import static fintech.workflow.ActivityStatus.ACTIVE
import static fintech.workflow.ActivityStatus.COMPLETED

@Stepwise
class AssignStrategiesHandlerTest extends AbstractAlfaTest {

    @Autowired
    LoanApplicationService loanApplicationService

    @Autowired
    CalculationStrategyService calculationStrategyService

    @Autowired
    MockDecisionEngine mockDecisionEngine


    def "AssignStrategies: received 3 new strategies from DE and overwrite the default strategies by them"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def wf = client.toLoanWorkflow()
        def la = loanApplicationService.get(wf.getApplicationId())
        def defInterestStrategyId = la.getInterestStrategyId()
        def defPenaltyStrategyId = la.getPenaltyStrategyId()
        def defExtensionStrategyId = la.getExtensionStrategyId()
        def newInterestStrategyId = initNewInterestStrategy()//IX110
        def newPenaltyStrategyId = initNewPenaltyStrategy()//PA110
        def newExtensionStrategyId = initNewExtensionStrategy()//ED110

        and:
        mockDecisionEngine.setResponseForScenario(STRATEGY_SCENARIO, { ->
            new DecisionResult()
                .setDecision("Valid")
                .setStatus(DecisionRequestStatus.OK)
                .setScore(BigDecimal.TEN)
                .setUsedFields(Collections.emptyList())
                .setVariablesResult(new HashMap<>())
                .setArrayResult(Arrays.asList("PA110", "ED110", "IX110"))
        })

        when:
        wf.runBeforeActivity(DOWJONES)

        then:
        checkIsLoanApplicationStrategiesChanged(wf.getApplicationId(), newInterestStrategyId, newPenaltyStrategyId, newExtensionStrategyId)
        !checkIsLoanApplicationStrategiesChanged(wf.getApplicationId(), defInterestStrategyId, defPenaltyStrategyId, defExtensionStrategyId)
        wf.getActivity(ASSIGN_STRATEGIES).resolution == OK
        wf.getActivity(ASSIGN_STRATEGIES).status == COMPLETED
        wf.getWorkflow().status == WorkflowStatus.ACTIVE
        loanApplicationService.get(wf.getApplicationId()).statusDetail == PENDING
    }


    @Unroll
    def "AssignStrategies: received empty arrayResult/throw exception from DE -> '#de_strategies', default strategies have not changed"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def wf = client.toLoanWorkflow()
        def la = loanApplicationService.get(wf.getApplicationId())
        def defInterestStrategyId = la.getInterestStrategyId()
        def defPenaltyStrategyId = la.getPenaltyStrategyId()
        def defExtensionStrategyId = la.getExtensionStrategyId()

        and:
        mockDecisionEngine.setResponseForScenario(STRATEGY_SCENARIO, { ->
            if (de_strategies == null ) {
                throw new RuntimeException("Decision-Engine test exception when scenario=" + STRATEGY_SCENARIO + ", arrayResult=null")
            } else {
                new DecisionResult()
                    .setDecision("Valid")
                    .setStatus(DecisionRequestStatus.OK)
                    .setScore(BigDecimal.TEN)
                    .setUsedFields(Collections.emptyList())
                    .setVariablesResult(new HashMap<>())
                    .setArrayResult(de_strategies)
            }
        })
        when:
        wf.runBeforeActivity(DOWJONES)

        then:
        checkIsLoanApplicationStrategiesChanged(wf.getApplicationId(), defInterestStrategyId, defPenaltyStrategyId, defExtensionStrategyId)
        wf.getActivity(ASSIGN_STRATEGIES).resolution == resolution
        wf.getActivity(ASSIGN_STRATEGIES).status == status
        wf.getWorkflow().status == wf_status
        loanApplicationService.get(wf.getApplicationId()).statusDetail == app_status_detail

        where:
        de_strategies           | wf_status             | resolution | status    | app_status_detail
        Collections.emptyList() | WorkflowStatus.ACTIVE | SKIP       | COMPLETED | PENDING
        null                    | WorkflowStatus.ACTIVE | null       | ACTIVE    | PENDING
    }


    def "AssignStrategies: received 1 new strategy from DE and overwrite the default strategy by it, two other strategies have not changed"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def wf = client.toLoanWorkflow()
        def la = loanApplicationService.get(wf.getApplicationId())
        def defInterestStrategyId = la.getInterestStrategyId()
        def defPenaltyStrategyId = la.getPenaltyStrategyId()
        def defExtensionStrategyId = la.getExtensionStrategyId()
        def newInterestStrategyId = initNewInterestStrategy()//IX110

        and:
        mockDecisionEngine.setResponseForScenario(STRATEGY_SCENARIO, { ->
            new DecisionResult()
                .setDecision("Valid")
                .setStatus(DecisionRequestStatus.OK)
                .setScore(BigDecimal.TEN)
                .setUsedFields(Collections.emptyList())
                .setVariablesResult(new HashMap<>())
                .setArrayResult(Arrays.asList("PA110", "ED110", "IX110"))
        })

        when:
        wf.runBeforeActivity(DOWJONES)

        then:
        checkIsLoanApplicationStrategiesChanged(wf.getApplicationId(), newInterestStrategyId, defPenaltyStrategyId, defExtensionStrategyId)
        !checkIsLoanApplicationStrategiesChanged(wf.getApplicationId(), defInterestStrategyId, defPenaltyStrategyId, defExtensionStrategyId)
        wf.getActivity(ASSIGN_STRATEGIES).resolution == OK
        wf.getActivity(ASSIGN_STRATEGIES).status == COMPLETED
        wf.getWorkflow().status == WorkflowStatus.ACTIVE
        loanApplicationService.get(wf.getApplicationId()).statusDetail == PENDING
    }


    def initNewInterestStrategy() {
        def command = new SaveCalculationStrategyCommand(
            strategyType: "I",
            calculationType: "X",
            version: "110",
            properties: ["monthlyInterestRate": 37],
            enabled: true,
            isDefault: false)
        def newInterestStrategyId = calculationStrategyService.saveCalculationStrategy(command)
        return newInterestStrategyId
    }


    def initNewPenaltyStrategy() {
        def command = new SaveCalculationStrategyCommand(
            strategyType: "P",
            calculationType: "A",
            version: "110",
            properties: ["penaltyRate": 10],
            enabled: true,
            isDefault: false)
        def newPenaltyStrategyId = calculationStrategyService.saveCalculationStrategy(command)
        return newPenaltyStrategyId
    }


    def initNewExtensionStrategy() {
        def command = new SaveCalculationStrategyCommand(
            strategyType: "E",
            calculationType: "D",
            version: "110",
            properties: ["extensions": [["rate": 1E+2, "term": 70], ["rate": 140, "term": 140], ["rate": 260, "term": 300]]],
            enabled: true,
            isDefault: false)
        def newExtensionStrategyId = calculationStrategyService.saveCalculationStrategy(command)
        return newExtensionStrategyId
    }

    def checkIsLoanApplicationStrategiesChanged(Long applicationId, Long interestStrategyId, Long penaltyStrategyId, Long extensionStrategyId) {
        def la = loanApplicationService.get(applicationId)
        assert la != null && interestStrategyId != null && penaltyStrategyId != null && extensionStrategyId != null
        return interestStrategyId.equals(la.getInterestStrategyId()) && penaltyStrategyId.equals(la.getPenaltyStrategyId()) && extensionStrategyId.equals(la.getExtensionStrategyId())
    }

}
