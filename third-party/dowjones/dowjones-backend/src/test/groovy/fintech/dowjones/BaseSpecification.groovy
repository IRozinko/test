package fintech.dowjones

import fintech.dowjones.db.DowJonesRequestEntityRepository
import fintech.dowjones.impl.MockDowJonesProviderBean
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    MockDowJonesProviderBean mockProvider

    @Autowired
    DowJonesService service

    @Autowired
    DowJonesRequestEntityRepository repository

    def setup() {
        testDatabase.cleanDb()
        mockProvider.setThrowError(false)
    }
}
