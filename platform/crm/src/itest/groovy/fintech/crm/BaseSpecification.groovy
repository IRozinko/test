package fintech.crm

import fintech.crm.client.ClientService
import fintech.crm.client.CreateClientCommand
import fintech.testing.integration.AbstractBaseSpecification
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    ClientService clientService

    def setup() {
        testDatabase.cleanDb()
    }

    Long createClient() {
        clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
    }
}
