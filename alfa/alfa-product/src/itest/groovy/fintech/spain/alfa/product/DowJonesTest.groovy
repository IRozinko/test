package fintech.spain.alfa.product

import fintech.JsonUtils
import fintech.crm.client.ClientService
import fintech.dowjones.db.SearchResultEntityRepository
import fintech.dowjones.impl.MockDowJonesProviderBean
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.LoanApplicationStatus
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.settings.commands.UpdatePropertyCommand
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.task.TaskService
import fintech.task.command.CompleteTaskCommand
import fintech.task.model.TaskQuery
import fintech.task.model.TaskStatus
import fintech.workflow.ActivityStatus
import org.springframework.beans.factory.annotation.Autowired

import static fintech.spain.alfa.product.settings.AlfaSettings.DOWJONES_SETTINGS
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.DECISION_ENG_ID_VALIDATION_2
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.DOWJONES
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.DOWJONES_MANUAL_CHECK
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.ID_DOCUMENT_MANUAL_TEXT_EXTRACTION
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.ID_DOCUMENT_MANUAL_VALIDATION
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.PREPARE_OFFER

class DowJonesTest extends AbstractAlfaTest {

    @Autowired
    SearchResultEntityRepository searchResultEntityRepository

    @Autowired
    ClientService clientService

    @Autowired
    TaskService taskService

    @Autowired
    LoanApplicationService loanApplicationService

    @Autowired
    MockDowJonesProviderBean mockProvider

    def "DowJones response with no client's matches"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
        def client = clientService.get(workflow.workflow.clientId)
        then:
        def searchResult = searchResultEntityRepository.findFirstByRequestId(workflow.workflow.attributeAsLong(fintech.spain.alfa.product.workflow.common.Attributes.DOWJONES_REQUEST_ID)).get()
        def matches = searchResult.matches
        matches.size() == 4
        matches.forEach({ match ->
            match.getLastName() != client.lastName
            match.getFirstName() != client.firstName
        })
    }

    def "DowJones response with matching client's first and last name"() {
        when:
        def testClient = fintech.spain.alfa.product.testing.TestFactory.newClient();
        testClient.firstName = 'Aleksandr'
        testClient.lastName = 'Medvedev'
        def workflow = testClient
            .signUp()
            .toLoanWorkflow()
            .runAll()
        def client = clientService.get(workflow.workflow.clientId)
        then:
        def searchResult = searchResultEntityRepository.findFirstByRequestId(workflow.workflow.attributeAsLong(fintech.spain.alfa.product.workflow.common.Attributes.DOWJONES_REQUEST_ID)).get()
        def matches = searchResult.matches
        matches.size() == 4
        matches.stream().filter({ match ->
            match.firstName.equalsIgnoreCase(client.firstName)
            match.lastName.equalsIgnoreCase(client.lastName)
        }).findFirst().isPresent()
    }

    def "DowJones manual check task run just after 'DowJones' step "() {

        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        def workflow = client
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(DOWJONES)

        then:
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.ACTIVE
        workflow.getActivityStatus(DOWJONES_MANUAL_CHECK) == ActivityStatus.WAITING

        when:
        workflow.runAfterActivity(DOWJONES)

        then:
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.COMPLETED
        workflow.getActivityStatus(DOWJONES_MANUAL_CHECK) == ActivityStatus.ACTIVE
    }

    def "DowJones step failed "() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        mockProvider.setThrowError(true)

        when:
        def workflow = client
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(DOWJONES)

        then:
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.ACTIVE

        when:
        workflow.runAfterActivity(DOWJONES)

        then:
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.COMPLETED
        workflow.getActivityResolution(DOWJONES) == fintech.spain.alfa.product.workflow.common.Resolutions.FAIL
        workflow.getActivityStatus(DOWJONES_MANUAL_CHECK) == ActivityStatus.CANCELLED
        loanApplicationService.get(workflow.applicationId).status == LoanApplicationStatus.CLOSED
        loanApplicationService.get(workflow.applicationId).statusDetail == LoanApplicationStatusDetail.CANCELLED
    }

    def "DowJones manual check completes with status REJECTED"() {

        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        def workflow = client
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(DOWJONES)

        then:
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.ACTIVE
        workflow.getActivityStatus(DOWJONES_MANUAL_CHECK) == ActivityStatus.WAITING

        when:
        workflow.runAfterActivity(DOWJONES)

        then:
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.COMPLETED
        workflow.getActivityStatus(DOWJONES_MANUAL_CHECK) == ActivityStatus.ACTIVE

        when:
        def task = taskService.findTasks(TaskQuery.byWorkflowId(workflow.getWorkflowId())).find {it.taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.DowJonesCheckTask.TYPE}

        then:
        task.status == TaskStatus.OPEN

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: task.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.DowJonesCheckTask.REJECT))

        then:
        workflow.getActivity(DOWJONES_MANUAL_CHECK).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(DOWJONES_MANUAL_CHECK).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT

        and:
        with(workflow.taskOfActivity(DOWJONES_MANUAL_CHECK).getTask()) {
            status == TaskStatus.COMPLETED
            resolution == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.DowJonesCheckTask.REJECT
        }
    }

    def "DowJones manual check task skipped"() {

        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        def workflow = client
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(DOWJONES)

        then:
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.ACTIVE
        workflow.getActivityStatus(DOWJONES_MANUAL_CHECK) == ActivityStatus.WAITING

        when:
        workflow.runBeforeActivity(DOWJONES)
        workflow.completeActivity(DOWJONES, fintech.spain.alfa.product.workflow.common.Resolutions.SKIP)

        then:
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.COMPLETED
        workflow.getActivityResolution(DOWJONES) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP

        when:
        workflow.runBeforeActivity(DOWJONES_MANUAL_CHECK)

        then:
        workflow.getActivityStatus(DOWJONES_MANUAL_CHECK) == ActivityStatus.COMPLETED
        workflow.getActivityResolution(DOWJONES_MANUAL_CHECK) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(PREPARE_OFFER) == ActivityStatus.ACTIVE

    }

    def "DowJones activity runs after id document verification"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        def workflow = client
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(DOWJONES)

        then:
        workflow.getActivityStatus(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION) == ActivityStatus.COMPLETED
        workflow.getActivityStatus(ID_DOCUMENT_MANUAL_VALIDATION) == ActivityStatus.COMPLETED
        workflow.getActivityStatus(DECISION_ENG_ID_VALIDATION_2) == ActivityStatus.COMPLETED
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.ACTIVE
        workflow.getActivityStatus(DOWJONES_MANUAL_CHECK) == ActivityStatus.WAITING

    }

    def "DowJones activity skipped"() {
        given:
        skipDowjones()
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        def workflow = client
            .signUp()
            .toLoanWorkflow()
            .runAfterActivity(DOWJONES)

        then:
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.COMPLETED
        workflow.getActivityResolution(DOWJONES) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(DOWJONES_MANUAL_CHECK) == ActivityStatus.COMPLETED
        workflow.getActivityResolution(DOWJONES_MANUAL_CHECK) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP

    }

    def "DowJones activity skipped for affiliates workflow"() {
        given:
        skipDowjones()
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()

        when:
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runAfterActivity(DOWJONES)

        then:
        workflow.getActivityStatus(DOWJONES) == ActivityStatus.COMPLETED
        workflow.getActivityResolution(DOWJONES) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(DOWJONES_MANUAL_CHECK) == ActivityStatus.COMPLETED
        workflow.getActivityResolution(DOWJONES_MANUAL_CHECK) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
    }

    def skipDowjones() {
        AlfaSettings.DowjonesSettings settings = settingsService.getJson(DOWJONES_SETTINGS, AlfaSettings.DowjonesSettings.class);
        settings.setEnabled(false)
        settingsService.update(new UpdatePropertyCommand(name: DOWJONES_SETTINGS, textValue: JsonUtils.writeValueAsString(settings)))
    }

}
