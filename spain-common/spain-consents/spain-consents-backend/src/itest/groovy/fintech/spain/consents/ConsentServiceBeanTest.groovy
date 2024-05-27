package fintech.spain.consents

import fintech.spain.consents.db.ConsentRepository
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

class ConsentServiceBeanTest extends AbstractBaseSpecification {

    @Autowired
    ConsentRepository consentRepository

    def "Test schema generation"() {
        expect:
        consentRepository.findAll().isEmpty()
    }

}
