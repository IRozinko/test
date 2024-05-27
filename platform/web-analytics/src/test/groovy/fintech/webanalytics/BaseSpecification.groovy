package fintech.webanalytics

import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    WebAnalyticsService service

    def setup() {
        testDatabase.cleanDb()
    }

}
