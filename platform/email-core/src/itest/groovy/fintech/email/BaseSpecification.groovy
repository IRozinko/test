package fintech.email

import fintech.email.impl.EmailSender
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    EmailSender sender

    def setup() {
        sender.setWhitelistedDomains("")
        sender.setWhitelistedEmails("")
        testDatabase.cleanDb()
    }
}
