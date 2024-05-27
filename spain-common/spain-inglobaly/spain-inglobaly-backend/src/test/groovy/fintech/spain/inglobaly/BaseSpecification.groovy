package fintech.spain.inglobaly

import fintech.spain.inglobaly.impl.MockInglobalyProvider
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    InglobalyService service

    @Autowired
    MockInglobalyProvider mockProvider

    def setup() {
        testDatabase.cleanDb()
        mockProvider.returnNotFound()
    }
}
