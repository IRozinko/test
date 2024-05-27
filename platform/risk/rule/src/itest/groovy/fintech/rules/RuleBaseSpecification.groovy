package fintech.rules

import fintech.testing.integration.AbstractBaseSpecification

abstract class RuleBaseSpecification extends AbstractBaseSpecification {

    def setup() {
        testDatabase.cleanDb()
    }
}
