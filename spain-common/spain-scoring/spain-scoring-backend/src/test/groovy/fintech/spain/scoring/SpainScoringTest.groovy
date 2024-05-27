package fintech.spain.scoring

import fintech.spain.scoring.model.ScoringModelType
import fintech.spain.scoring.model.ScoringQuery
import fintech.spain.scoring.model.ScoringRequestCommand
import fintech.spain.scoring.model.ScoringRequestStatus
import fintech.spain.scoring.spi.ScoringResponse

class SpainScoringTest extends BaseSpecification {


    def "Success"() {
        given:
        mockProvider.setResponse(ScoringModelType.DEDICATED_MODEL, ScoringResponse.ok(200, "MOCK", 333.33)
            .setDecision("Approved").setRating("A111111"))

        when:
        def score = scoringService.requestScore(new ScoringRequestCommand(type: ScoringModelType.DEDICATED_MODEL, clientId: 101L, attributes: [amount: 1000.00]))

        then:
        score.score == 333.33d
        score.status == ScoringRequestStatus.OK
        score.clientId == 101L
        score.decision == "Approved"
        score.rating == "A111111"
    }

    def "Request fails"() {
        given:
        mockProvider.setResponse(ScoringModelType.DEDICATED_MODEL, ScoringResponse.error(500, "MOCK", "error"))

        when:
        def score = scoringService.requestScore(new ScoringRequestCommand(type: ScoringModelType.DEDICATED_MODEL, clientId: 101L, attributes: [amount: 1000.00]))

        then:
        score.score == -1d
        score.status == ScoringRequestStatus.ERROR
    }

    def "Unexpected error"() {
        given:
        mockProvider.setThrowError(true)

        when:
        def score = scoringService.requestScore(new ScoringRequestCommand(type: ScoringModelType.DEDICATED_MODEL, clientId: 101L, attributes: [amount: 1000.00]))

        then:
        score.score == -1d
        score.status == ScoringRequestStatus.ERROR
    }

    def "Find latest"() {
        expect:
        !scoringService.findLatest(new ScoringQuery(type: ScoringModelType.DEDICATED_MODEL)).isPresent()

        when:
        mockProvider.setResponse(ScoringModelType.DEDICATED_MODEL, ScoringResponse.ok(200, "MOCK", 0.1))
        scoringService.requestScore(new ScoringRequestCommand(type: ScoringModelType.DEDICATED_MODEL, clientId: 1L, applicationId: 2L, attributes: [amount: 1000.00]))

        then:
        scoringService.findLatest(new ScoringQuery(type: ScoringModelType.DEDICATED_MODEL, clientId: 1L, applicationId: 2L, statuses: [ScoringRequestStatus.OK])).get().score == 0.1d

        when:
        mockProvider.setResponse(ScoringModelType.DEDICATED_MODEL, ScoringResponse.ok(200, "MOCK", 0.2))
        scoringService.requestScore(new ScoringRequestCommand(type: ScoringModelType.DEDICATED_MODEL, clientId: 1L, applicationId: 2L, attributes: [amount: 1000.00]))

        then:
        scoringService.findLatest(new ScoringQuery(type: ScoringModelType.DEDICATED_MODEL, clientId: 1L, applicationId: 2L, statuses: [ScoringRequestStatus.OK])).get().score == 0.2d

        and:
        !scoringService.findLatest(new ScoringQuery(type: ScoringModelType.LINEAR_REGRESSION_MODEL)).isPresent()
        !scoringService.findLatest(new ScoringQuery(type: ScoringModelType.DEDICATED_MODEL, clientId: 1L, applicationId: 3L, statuses: [ScoringRequestStatus.OK])).isPresent()
        !scoringService.findLatest(new ScoringQuery(type: ScoringModelType.DEDICATED_MODEL, clientId: 2L, applicationId: 2L, statuses: [ScoringRequestStatus.OK])).isPresent()
        !scoringService.findLatest(new ScoringQuery(type: ScoringModelType.DEDICATED_MODEL, clientId: 1L, applicationId: 2L, statuses: [ScoringRequestStatus.ERROR])).isPresent()
        !scoringService.findLatest(new ScoringQuery(type: ScoringModelType.LINEAR_REGRESSION_MODEL, clientId: 1L, applicationId: 2L, statuses: [ScoringRequestStatus.OK])).isPresent()
    }
}
