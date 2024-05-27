package fintech.admintools

import fintech.admintools.db.AdminActionLogRepository
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    AdminToolsService service

    @Autowired
    AdminActionLogRepository logRepository

    def setup() {
        testDatabase.cleanDb()
    }
}
