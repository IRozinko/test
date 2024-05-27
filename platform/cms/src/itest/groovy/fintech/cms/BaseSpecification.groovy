package fintech.cms

import fintech.cms.spi.CmsRegistry
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    CmsRegistry registry

    def setup() {
        testDatabase.cleanDb()
    }
}
