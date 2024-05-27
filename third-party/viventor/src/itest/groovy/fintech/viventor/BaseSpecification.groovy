package fintech.viventor

import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    ViventorService service

    def setup() {
        testDatabase.cleanDb()
    }
}
