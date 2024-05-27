package fintech.spain.alfa.product.workflow.upsell

import fintech.TimeMachine
import fintech.settings.SettingsService
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.workflow.WorkflowQuery
import fintech.workflow.WorkflowService
import fintech.workflow.WorkflowStatus
import org.springframework.beans.factory.annotation.Autowired

import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER

class UpsellWorkflowConsumerTest extends AbstractAlfaTest {

    @Autowired
    UpsellWorkflowConsumer consumer
    @Autowired
    SettingsService settingsService
    @Autowired
    WorkflowService workflowService
    @Autowired
    fintech.spain.alfa.product.lending.UnderwritingFacade underwritingFacade

    def setup() {
        def settings = settingsService.getJson(AlfaSettings.UPSELL_WITHIN_NEW_LOAN_APPLICATION_SETTINGS, AlfaSettings.UpsellWithinNewLoanApplicationSettings.class)
        settings.setMaxPrincipalRepaid(50.00)
        saveJsonSettings(AlfaSettings.UPSELL_WITHIN_NEW_LOAN_APPLICATION_SETTINGS, settings)

        settings = settingsService.getJson(AlfaSettings.APPLICATION_OF_UPSELL_SETTINGS, AlfaSettings.ApplicationOfUpsellSettings.class)
        settings.setMinDaysSinceLoanIssue(0)
        settings.setMinDaysBeforeEndOfTerm(30)
        saveJsonSettings(AlfaSettings.APPLICATION_OF_UPSELL_SETTINGS, settings)
    }

    def "Start Upsell wortkflow for eligible client"() {
        given:
        def client = repeatedClient()

        when: "Client has active loan (eligible for Upsell)"
        client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()

        and: "Uspell scheduler runs"
        consumer.consume()

        then:
        client.toWorkflow(UpsellWorkflow.WORKFLOW, fintech.spain.alfa.product.testing.TestUpsellWorkflow.class)
        client.toWorkflow(UpsellWorkflow.WORKFLOW, fintech.spain.alfa.product.testing.TestUpsellWorkflow.class).isActive()
    }

    def "Try to start Upsell wortkflow for eligible client without accepting marketing"() {
        given:
        def client = repeatedClient()

        when: "Client refuse accept marketing"
        client.acceptMarketing(false)

        and: "Client has active loan (eligible for Upsell)"
        client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()

        and: "Uspell scheduler runs"
        consumer.consume()
        consumer.terminateWorkflows(client.clientId)

        then:
        workflowService.findWorkflows(WorkflowQuery.byClientId(client.clientId, WorkflowStatus.ACTIVE)).isEmpty()
    }

    def "Upsell workflow issued in case if loan was not already upsold"() {
        given: "Client with paid loan"
        def client = repeatedClient()

        when: "Client submit loan application for less credit limit"
        client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()

        and: "Run upsell scheduler consumer"
        consumer.consume()

        then: "Client is eligible for upsell workflow"
        workflowService.findWorkflows(WorkflowQuery.byClientId(client.clientId, UpsellWorkflow.WORKFLOW, WorkflowStatus.ACTIVE)).size() == 1
    }

    def "Upsell workflow not issued in case if loan was already upsold"() {
        given: "Client with paid loan"
        def client = repeatedClient()

        when: "Client submit loan application for less credit limit"
        client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runBeforeActivity(APPROVE_LOAN_OFFER)

        and: "Client accepted upsell offer"
        def upsellAmount = 370.00
        underwritingFacade.webApproveUpsellOffer(client.clientId, client.toLoanWorkflow().toApplication().application.id, upsellAmount, "00:00:00:00", null)
        client.toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()

        and: "Run upsell scheduler consumer"
        consumer.consume()

        then: "Starting upsell workflow is skipped"
        workflowService.findWorkflows(WorkflowQuery.byClientId(client.clientId, UpsellWorkflow.WORKFLOW, WorkflowStatus.ACTIVE)).size() == 0
    }

    def repeatedClient() {
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setDateOfBirth(TimeMachine.today().minusYears(36))
            .setAmount(1000.00)
            .signUp()

        2.times {
            client
                .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
                .toLoanWorkflow()
                .runAll()
                .exportDisbursement()
                .toLoan()
                .repayAll(TimeMachine.today())
        }
        return client
    }
}
