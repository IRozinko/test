package fintech.spain.experian

import fintech.testing.integration.AbstractBaseSpecification

class BaseSpecification extends AbstractBaseSpecification {

    def setup() {
        testDatabase.cleanDb()
    }
}
