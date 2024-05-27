package fintech.affiliate

import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    AffiliateService service

    def setup() {
        testDatabase.cleanDb()
    }

}
