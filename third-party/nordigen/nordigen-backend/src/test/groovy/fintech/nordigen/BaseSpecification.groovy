package fintech.nordigen

import fintech.nordigen.impl.MockNordigenProvider
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    MockNordigenProvider mockProvider

    @Autowired
    NordigenService service

    def setup() {
        testDatabase.cleanDb()
        mockProvider.setResponse(MockNordigenProvider.okResponse("ES1910240863456024678400"))
        mockProvider.setThrowError(false)
    }
}
