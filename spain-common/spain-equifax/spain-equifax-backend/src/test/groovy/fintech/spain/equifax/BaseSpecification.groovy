package fintech.spain.equifax

import fintech.spain.equifax.mock.MockEquifaxProvider
import fintech.spain.equifax.mock.MockedEquifaxResponse
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    EquifaxService service

    @Autowired
    MockEquifaxProvider mockProvider

    def setup() {
        testDatabase.cleanDb()
        mockProvider.setResponseSupplier(MockedEquifaxResponse.DEFAULT)
        mockProvider.setThrowError(false)
    }
}
