package fintech.risk.checklist

import fintech.testing.integration.AbstractBaseSpecification

class BaseSpecification extends AbstractBaseSpecification {

    def setup() {
        testDatabase.cleanDb()
    }
}
