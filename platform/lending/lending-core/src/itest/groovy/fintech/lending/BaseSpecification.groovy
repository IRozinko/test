package fintech.lending

import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner

abstract class BaseSpecification extends AbstractBaseSpecification {

    def setup() {
        testDatabase.cleanDb()
    }
}
