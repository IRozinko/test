package fintech.filestorage

import fintech.testing.integration.AbstractBaseSpecification


abstract class BaseSpecification extends AbstractBaseSpecification {

    def setup() {
        testDatabase.cleanDb()
    }
}
