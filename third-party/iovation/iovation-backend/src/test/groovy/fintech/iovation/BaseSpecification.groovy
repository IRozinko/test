package fintech.iovation

import fintech.iovation.impl.MockIovationProvider
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    MockIovationProvider mockProvider

    @Autowired
    IovationService service

    def setup() {
        testDatabase.cleanDb()
        mockProvider.setThrowError(false)
    }
}
