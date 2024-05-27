package fintech.spain.scoring


import fintech.spain.scoring.impl.MockSpainScoringProvider
import fintech.spain.scoring.model.ScoringModelType
import fintech.spain.scoring.spi.ScoringResponse
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    MockSpainScoringProvider mockProvider

    @Autowired
    SpainScoringService scoringService

    def setup() {
        testDatabase.cleanDb()
        mockProvider.setResponse(ScoringModelType.DEDICATED_MODEL, ScoringResponse.ok(200, "MOCK", 333.33))
        mockProvider.setResponse(ScoringModelType.LINEAR_REGRESSION_MODEL, ScoringResponse.ok(200, "MOCK", 333.33))
        mockProvider.setResponse(ScoringModelType.CREDIT_LIMIT_MODEL, ScoringResponse.ok(200, "MOCK", 333.33))
        mockProvider.setThrowError(false)
    }
}
