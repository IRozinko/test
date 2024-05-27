package fintech.spain.alfa.product.workflow.undewrtiting.handlers

import fintech.JsonUtils
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.strategy.interest.AlfaInterestStrategy
import fintech.spain.scoring.model.ScoringModelType
import fintech.spain.scoring.model.ScoringQuery
import fintech.spain.scoring.model.ScoringRequestStatus
import fintech.spain.scoring.model.ScoringResult
import fintech.spain.alfa.strategy.StrategyType
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties
import fintech.strategy.CalculationStrategyService
import fintech.strategy.SaveCalculationStrategyCommand
import fintech.workflow.ActivityStatus
import fintech.workflow.WorkflowStatus
import org.springframework.beans.factory.annotation.Autowired

import static fintech.BigDecimalUtils.amount
import static fintech.spain.alfa.product.scoring.ScoringRequestAttributes.APPLICATION_ID
import static fintech.spain.alfa.product.scoring.ScoringRequestAttributes.CLIENT_ID
import static fintech.spain.alfa.product.scoring.ScoringRequestAttributes.WORKFLOW_ID
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.PREPARE_OFFER

class PrepareOfferActivityTest extends AbstractAlfaTest {

    @Autowired
    CalculationStrategyService calculationStrategyService

    def "PrepareOffer activity successfully completed - get interest rate from DE"() {
        given:
        def score = amount(32)
        mockSpainScoringProvider.useInterestRateResponse(score)
        def interestStrategyId = calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.INTEREST.getType())
                .setCalculationType(AlfaInterestStrategy.CALCULATION_TYPE.name())
                .setVersion("002")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new MonthlyInterestStrategyProperties()
                        .setMonthlyInterestRate(amount(50.00))
                        .setUsingDecisionEngine(true)
                        .setScenario("interest_rate_setting")
                ))

        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def wf = client.toLoanWorkflow()
        def application = wf.toApplication()
        application.application.setInterestStrategyId(interestStrategyId)

        when:
        wf.runAfterActivity(PREPARE_OFFER)

        then:
        Optional<ScoringResult> result = scoringService.findLatest(new ScoringQuery(type: ScoringModelType.INTEREST_RATE_MODEL,
            clientId: client.getClientId(),
            applicationId: client.getApplicationId(),
            statuses: [ScoringRequestStatus.OK]))
        result.isPresent()
        with(result.get()) {
            !it.requestAttributes.isEmpty()
            def attributes = JsonUtils.readValueAsMap(it.requestAttributes)
            attributes[APPLICATION_ID] == client.applicationId
            attributes[CLIENT_ID] == client.getClientId()
            attributes[WORKFLOW_ID] == wf.getWorkflowId()
            it.getDecision() == 'A'
            it.getRating() == 'A'
            it.getScore() == score
        }
        wf.getWorkflow().status == WorkflowStatus.ACTIVE
        wf.getActivity(PREPARE_OFFER).status == ActivityStatus.COMPLETED

        cleanup:
        mockSpainScoringProvider.reset()
    }
}
