package fintech.sms

import fintech.sms.impl.SmsSender
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    SmsSender sender

    def setup() {
        testDatabase.cleanDb()
        sender.setWhitelistedNumbers("")
    }
}
