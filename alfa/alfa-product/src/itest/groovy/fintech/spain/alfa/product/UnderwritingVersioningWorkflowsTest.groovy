package fintech.spain.alfa.product

import fintech.TimeMachine


import fintech.workflow.WorkflowService
import fintech.workflow.spi.WorkflowRegistry
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class UnderwritingVersioningWorkflowsTest extends AbstractAlfaTest {

    @Autowired
    WorkflowService workflowService

    @Autowired
    WorkflowRegistry workflowRegistry

    @Autowired
    private fintech.spain.alfa.product.lending.UnderwritingFacade underwritingFacade

    //In case if new version created please update the tests
    @Unroll
    def "Submitting underwriting loan application start workflow with latest version #latestExpectedVersion "() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        client.signUp()

        then:
        client.toLoanWorkflow().workflow.version == latestExpectedVersion

        where:
        latestExpectedVersion << 20
    }

    def "Underwriting workflow with old version finishes with success and new one is used further"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when: "We have started active old version of workflow in the system"
        workflowRegistry.clear()
        workflowRegistry.addDefinition({ underwritingWorkflowV0.firstLoanWorkflow() }, 30)
        def oldWorkflowVersion = client.signUp().toLoanWorkflow()
        workflowRegistry.addDefinition({ underwritingWorkflowV1.firstLoanWorkflow() }, 30)

        then:
        oldWorkflowVersion.workflow.version == 0

        when: "Old Active workflow finished"
        oldWorkflowVersion.runAll().exportDisbursement()

        then: "Workflow with old version is completed with success"
        oldWorkflowVersion.workflow.version == 0
        oldWorkflowVersion.isCompleted()

        when: "Start new workflow"
        oldWorkflowVersion.toLoan().repayAll(TimeMachine.today())
        client.submitApplicationAndStartFirstLoanWorkflow(200.00, 15, TimeMachine.today())
        def newWorkflowVersion = client.toLoanWorkflow()

        then: "Started workflow with new version"
        newWorkflowVersion.workflow.version == 1

        when: "New Active workflow finished"
        newWorkflowVersion.runAll().exportDisbursement()


        then: "Workflow with new version is completed with success"
        newWorkflowVersion.workflow.version == 1
        newWorkflowVersion.isCompleted()
    }

    def "Retry underwriting workflow with old version starts workflow with new version"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when: "We have started active old version of workflow in the system"
        workflowRegistry.clear()
        workflowRegistry.addDefinition({ underwritingWorkflowV0.firstLoanWorkflow() }, 30)
        def oldWorkflowVersion = client.signUp().toLoanWorkflow()
        workflowRegistry.addDefinition({ underwritingWorkflowV1.firstLoanWorkflow() }, 30)

        then:
        oldWorkflowVersion.workflow.version == 0

        when: "Retry loan application"
        underwritingFacade.retryApplication(oldWorkflowVersion.toApplication().application.id)

        then: "Latest version of workflow is started"
        oldWorkflowVersion.isTerminated()
        client.toLoanWorkflow().workflow.version == 1

        when:
        def newWorkflowVersion = client.toLoanWorkflow().runAll().exportDisbursement()

        then:
        newWorkflowVersion.isCompleted()
    }

}
