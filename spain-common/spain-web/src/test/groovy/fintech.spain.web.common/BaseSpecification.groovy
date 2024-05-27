package fintech.spain.web.common

import fintech.cms.spi.CmsRegistry
import fintech.testing.integration.AbstractBaseSpecification
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource

class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    ValidationExceptions validationExceptions

    @Autowired
    CmsRegistry cmsRegistry

    @SpringBean
    MessageSource messageSource = new CmsBasedMessageSource("localization", "es")

    def setup() {
        testDatabase.cleanDb()
        messageSource.setCmsRegistry(cmsRegistry)
    }
}
