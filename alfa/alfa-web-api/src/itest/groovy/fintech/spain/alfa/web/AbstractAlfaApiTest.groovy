package fintech.spain.alfa.web

import fintech.IntegrationApplication
import fintech.crm.client.ClientService
import fintech.lending.core.application.LoanApplicationService
import fintech.spain.alfa.product.CrmAlfaSetup
import fintech.testing.integration.AbstractBaseSpecification
import fintech.workflow.Activity
import fintech.workflow.Workflow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.support.TransactionTemplate
import spock.util.concurrent.PollingConditions

@ContextConfiguration
@ActiveProfiles("itest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = IntegrationApplication.class)
abstract class AbstractAlfaApiTest extends AbstractBaseSpecification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    LoanApplicationService applicationService

    @Autowired
    ClientService clientService

    @Autowired
    CrmAlfaSetup alfaSetup

    @Autowired
    ApiHelper apiHelper

    @Autowired
    ClientApiHelper clientApiHelper

    @Autowired
    TransactionTemplate txTemplate

    def conditions = new PollingConditions(timeout: 5)

    def setup() {
        testDatabase.cleanDb([])
        alfaSetup.setUp()
    }

    protected Optional<Activity> activeState(Workflow workflow) {
        return workflow.getActivities().stream()
            .filter { a -> a.isActive() }
            .findAny()
    }

    String getActivityResolution(Workflow workflow, String activity) {
        return workflow.activity(activity).resolution
    }

}
