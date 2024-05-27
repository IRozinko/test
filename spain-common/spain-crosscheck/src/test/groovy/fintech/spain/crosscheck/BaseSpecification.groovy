package fintech.spain.crosscheck

import fintech.spain.crosscheck.impl.MockSpainCrosscheckProvider
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    MockSpainCrosscheckProvider mockProvider

    @Autowired
    SpainCrosscheckService service

    def setup() {
        testDatabase.cleanDb()
        mockProvider.setResponse(MockSpainCrosscheckProvider.notFoundResponse())
        mockProvider.setThrowError(false)
    }
}
