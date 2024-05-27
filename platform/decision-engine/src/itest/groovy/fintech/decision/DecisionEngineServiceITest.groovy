package fintech.decision

import fintech.decision.db.DecisionEngineRequestRepository
import fintech.decision.db.ScoringValueUsageRepository
import fintech.decision.model.DecisionEngineRequest
import fintech.decision.model.DecisionRequestStatus
import fintech.decision.model.DecisionResult
import fintech.decision.spi.DecisionEngine
import fintech.scoring.values.ScoringValuesService
import fintech.scoring.values.db.ScoringValueSource
import fintech.scoring.values.spi.ScoringValuesProvider
import fintech.testing.integration.AbstractBaseSpecification
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("DecisionEngineServiceITest")
class DecisionEngineServiceITest extends AbstractBaseSpecification {

    @Autowired
    DecisionEngineService decisionEngineService

    @Autowired
    ScoringValueUsageRepository usageRepository

    @Autowired
    DecisionEngineRequestRepository requestRepository

    @Autowired
    ScoringValuesService scoringValuesService

    @SpringBean
    DecisionEngine engine = Stub()

    def "setup"() {
        engine.getDecision(_ as DecisionEngineRequest) >> { DecisionEngineRequest req ->
            return new DecisionResult(status: DecisionRequestStatus.OK, decision: "success", rating: "rating", usedFields: req.values, score: 10,
                variablesResult: ['creditLimit': 100.00])
        }
    }

    def "DecisionEngineService::GetDecision::Straight forward scenario"() {
        given:
        def scoringModel = scoringValuesService.collectValues(1)
        def req = new DecisionEngineRequest("scenario", 1, scoringModel.id)

        when:
        def result = decisionEngineService.getDecision(req)

        then: "scoring values collected, scoring values usage data was saved, new values from decision engine response were saved"
        result.decision == 'success'
        with(scoringValuesService.getValues(req.getScoringModelId())) {
            it.size() == 2
            it.find({
                it.source == ScoringValueSource.INNER
                it.key == 'key'
                it.value == 'value'
            }) != null
            it.find({
                it.source == ScoringValueSource.OUTER
                it.key == 'creditLimit'
                it.value == 100.00
            }) != null
        }
        with(usageRepository.findAll()) {
            it.size() == 1
            it[0].decisionEngineRequestId == result.getDecisionEngineRequestId()
            it[0].scoringKeys == ['key']
        }
    }

    @Configuration
    @Profile("DecisionEngineServiceITest")
    static class ContextConfiguration {

        @Bean("first_fake_provider")
        ScoringValuesProvider firstFakeProvider() {
            return { clientId -> new Properties(['key': 'value']) }
        }
    }

}
