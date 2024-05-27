package fintech.spain.alfa.product.loc

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.settings.commands.UpdatePropertyCommand
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.workflow.WorkflowQuery
import fintech.workflow.WorkflowService
import fintech.workflow.WorkflowStatus
import fintech.workflow.event.WorkflowCompletedEvent
import org.junit.Ignore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions

class LocBatchServiceTest extends AbstractAlfaTest {

    @Autowired
    LocBatchService locBatchService

    @Autowired
    WorkflowService workflowService

    def "UploadClients"() {
        given:
        updateMaxWFtoRun(2)
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        locBatchService.uploadClients([client1.clientId])

        locBatchService.uploadClients([client2.clientId])

        def entity1 = locBatchService.findOptional(new LocBatchQuery(clientId: client1.clientId))
        def entity2 = locBatchService.findOptional(new LocBatchQuery(clientId: client2.clientId))

        then:
        entity1.isPresent()
        entity2.isPresent()

        entity1.get().status == LocBatchStatus.PENDING
        entity2.get().status == LocBatchStatus.PENDING

        entity1.get().batchNumber != entity2.get().batchNumber
    }

    def "TriggerBatch"() {
        given:
        updateMaxWFtoRun(1)
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        locBatchService.uploadClients([client1.clientId, client2.clientId])

        and:
        def batchNumber = locBatchService.findOptional(new LocBatchQuery(clientId: client1.clientId)).get().batchNumber
        locBatchService.trigger(batchNumber)

        and:
        def entity1 = locBatchService.findOptional(new LocBatchQuery(clientId: client1.clientId)).get()
        def entity2 = locBatchService.findOptional(new LocBatchQuery(clientId: client2.clientId)).get()

        then:
        entity1.status == LocBatchStatus.STARTED
        entity2.status == LocBatchStatus.WAITING

        when:
        locBatchService.trigger(batchNumber)
        entity1 = locBatchService.findOptional(new LocBatchQuery(clientId: client1.clientId)).get()
        entity2 = locBatchService.findOptional(new LocBatchQuery(clientId: client2.clientId)).get()

        then:
        entity1.status == LocBatchStatus.STARTED
        entity2.status == LocBatchStatus.WAITING
    }

    def "Start Workflow After finished one"() {
        given:
        updateMaxWFtoRun(1)
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().cancelActiveApplication()
        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().cancelActiveApplication()

        when:
        def batch1 = locBatchService.uploadClients([client1.clientId, client2.clientId])
        def batch2 = locBatchService.uploadClients([client1.clientId])

        and:
        locBatchService.trigger(batch1)

        and:
        def entity1 = locBatchService.findOptional(new LocBatchQuery(clientId: client1.clientId, batchNumber: batch1)).get()
        def entity2 = locBatchService.findOptional(new LocBatchQuery(clientId: client2.clientId, batchNumber: batch1)).get()

        then:
        entity1.status == LocBatchStatus.STARTED
        entity2.status == LocBatchStatus.WAITING

        when:
        client1.toDormantsQualifyLocWorkflow().runAll()
        def locInstWf = client1.toDormantsInstantorLocWorkflow()
        workflowService.terminateWorkflow(locInstWf.workflowId, "")
        wfListener.handleWorkflowEvent(new WorkflowCompletedEvent(locInstWf.workflow))

        entity1 = locBatchService.findOptional(new LocBatchQuery(clientId: client1.clientId, batchNumber: batch1)).get()
        entity2 = locBatchService.findOptional(new LocBatchQuery(clientId: client2.clientId, batchNumber: batch1)).get()

        then:
        entity1.status == LocBatchStatus.COMPLETED
        entity2.status == LocBatchStatus.STARTED
    }


    def "StartWorkflows"() {
        given:
        updateMaxWFtoRun(2)
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        locBatchService.uploadClients([client.clientId])

        when:
        def batchNumber = locBatchService.findOptional(new LocBatchQuery(clientId: client.clientId)).get().batchNumber
        locBatchService.trigger(batchNumber)
        locBatchService.startWorkflows()

        def batchEntities = locBatchService.find(new LocBatchQuery(clientId: client.clientId, status: LocBatchStatus.STARTED))
        def workflows = workflowService.findWorkflows(WorkflowQuery.byClientId(client.clientId, DORMANTS_QUALIFY_LOC, WorkflowStatus.ACTIVE))

        then:
        batchEntities.size() == 1
        batchEntities[0].status == LocBatchStatus.STARTED
        workflows.size() == 1

    }

    def "Parse client's ids from input string"() {
        given:
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        def input = "$client1.clientId, $client2.clientId"
        locBatchService.uploadClients(input)

        and:
        def batchEntities = locBatchService.find(new LocBatchQuery(status: LocBatchStatus.PENDING))

        then:
        batchEntities.size() == 2
    }

    @Unroll
    def "Upload clients #input"() {
        when:
        locBatchService.uploadClients(input)

        then:
        thrown exception

        where:
        input   | exception
        '1234'  | JpaObjectRetrievalFailureException
        'sd123' | NumberFormatException
    }

    def "Failed batch entity"() {
        given:
        def polling = new PollingConditions(timeout: 10, initialDelay: 1, factor: 2)
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().cancelActiveApplication()
        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().submitApplication(new fintech.spain.alfa.product.lending.Inquiry(principal: 100.00,
            termInDays: 10, interestDiscountPercent: 0.0, submittedAt: TimeMachine.now())).toClient()
        def client3 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueLoan(700.00, 10L, TimeMachine.today())
            .exportDisbursements(TimeMachine.today())
            .settleDisbursements(TimeMachine.today())
            .toClient()

        def client4 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueLoan(1000.00, 10L, TimeMachine.today())
            .exportDisbursements(TimeMachine.today())
            .settleDisbursements(TimeMachine.today())
            .repayAll(TimeMachine.today())
            .toClient()
            .submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now())
            .toDormantsQualifyLocWorkflow().runAll()
            .toClient()

        when:
        def locWf = client4.toDormantsInstantorLocWorkflow().runAll()

        then:
        polling.eventually {
            assert locWf.workflow.status == WorkflowStatus.COMPLETED
        }

        when:
        def input = "$client1.clientId, $client2.clientId, $client3.clientId, $client4.clientId"
        locBatchService.uploadClients(input)

        and:
        def batchEntry = locBatchService.findOptional(new LocBatchQuery(clientId: client1.clientId)).get()
        locBatchService.trigger(batchEntry.batchNumber)

        then:
        def failedBatchEntry1 = locBatchService.findOptional(new LocBatchQuery(clientId: client1.clientId)).get()
        failedBatchEntry1.status == LocBatchStatus.FAILED
        failedBatchEntry1.statusDetail == LocBatchStatusDetail.HAS_ACTIVE_WF

        def failedBatchEntry2 = locBatchService.findOptional(new LocBatchQuery(clientId: client2.clientId)).get()
        failedBatchEntry2.status == LocBatchStatus.FAILED
        failedBatchEntry2.statusDetail == LocBatchStatusDetail.HAS_ACTIVE_APPLICATION

        def failedBatchEntry3 = locBatchService.findOptional(new LocBatchQuery(clientId: client3.clientId)).get()
        failedBatchEntry3.status == LocBatchStatus.FAILED
        failedBatchEntry3.statusDetail == LocBatchStatusDetail.HAS_OPEN_LOAN


        def failedBatchEntry4 = locBatchService.findOptional(new LocBatchQuery(clientId: client4.clientId)).get()
        failedBatchEntry4.status == LocBatchStatus.FAILED
        failedBatchEntry4.statusDetail == LocBatchStatusDetail.HAS_APPROVED_LOC
    }

    def client() {
        return fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().cancelActiveApplication()
    }


    @Ignore
    def updateMaxWFtoRun(int number) {
        def settings = settingsService.getJson(AlfaSettings.LOC_CLIENT_BATCH, AlfaSettings.LocClientBatch.class)
        settings.setMaxWorkflowsToRun(number)
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.LOC_CLIENT_BATCH,
            textValue: JsonUtils.writeValueAsString(settings)))
    }

}
