package fintech.activity

import fintech.activity.db.ActivityLogRepository
import fintech.activity.spi.ActivityRegistry
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    ActivityService service

    @Autowired
    ActivityRegistry registry

    @Autowired
    ActivityLogRepository repository

    def setup() {
        testDatabase.cleanDb()
        NoopActionHandler.executed = 0
        registry.registerBulkActionHandler("Noop", NoopActionHandler.class)
    }

}
