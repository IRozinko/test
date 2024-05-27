package fintech.scoring.values

import fintech.testing.integration.AbstractBaseSpecification

abstract class BaseSpecification extends AbstractBaseSpecification {

    def setup() {
        testDatabase.cleanDb()
    }

}
