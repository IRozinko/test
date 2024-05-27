package fintech.spain.alfa.product

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.lending.core.PeriodUnit
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.LoanApplicationStatus
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.lending.core.application.LoanApplicationType
import fintech.lending.core.discount.ApplyDiscountCommand
import fintech.lending.core.discount.DiscountService
import fintech.lending.core.loan.LoanService
import fintech.lending.core.loan.LoanStatusDetail
import fintech.settings.commands.UpdatePropertyCommand
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.task.TaskService
import fintech.task.model.TaskQuery
import fintech.task.model.TaskStatus
import fintech.task.spi.ExpiredTaskConsumer
import fintech.workflow.WorkflowQuery
import fintech.workflow.WorkflowService
import fintech.workflow.WorkflowStatus
import org.springframework.beans.factory.annotation.Autowired

class UpsellWorkflowTest extends AbstractAlfaTest {

    @Autowired
    fintech.spain.alfa.product.workflow.upsell.UpsellWorkflowConsumer upsellWorkflowConsumer

    @Autowired
    WorkflowService workflowService

    @Autowired
    LoanApplicationService loanApplicationService

    @Autowired
    ExpiredTaskConsumer expiredTaskConsumer

    @Autowired
    TaskService taskService

    @Autowired
    LoanService loanService

    @Autowired
    DiscountService discountService

    def "Don't start upsell workflow"() {
        when:
        TimeMachine.useFixedClockAt(TimeMachine.today().minusDays(daysToSubtract))

        and:
        client()
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()

        and:
        TimeMachine.useDefaultClock()

        and:
        upsellWorkflowConsumer.consume()

        then:
        workflowService.findWorkflows(WorkflowQuery.byWorkflowName(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.WORKFLOW, WorkflowStatus.ACTIVE)).size() == 0

        where:
        daysToSubtract << [2, 21]
    }

    def "Terminate upsell workflow - minDaysBeforeEndOfTerm"() {
        when:
        TimeMachine.useFixedClockAt(TimeMachine.today().minusDays(20))

        and:
        client()
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()

        and:
        TimeMachine.useDefaultClock()

        and:
        upsellWorkflowConsumer.consume()

        then:
        workflowService.findWorkflows(WorkflowQuery.byWorkflowName(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.WORKFLOW, WorkflowStatus.ACTIVE)).size() == 1
        workflowService.findWorkflows(WorkflowQuery.byWorkflowName(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.WORKFLOW, WorkflowStatus.TERMINATED)).size() == 0

        when:
        TimeMachine.useFixedClockAt(TimeMachine.today().plusDays(1))

        and:
        upsellWorkflowConsumer.consume()

        then:
        workflowService.findWorkflows(WorkflowQuery.byWorkflowName(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.WORKFLOW, WorkflowStatus.ACTIVE)).size() == 0
        workflowService.findWorkflows(WorkflowQuery.byWorkflowName(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.WORKFLOW, WorkflowStatus.TERMINATED)).size() == 1
    }

    def "Terminate upsell workflow - minDaysSinceLoanIssue"() {
        when:
        TimeMachine.useFixedClockAt(TimeMachine.today().minusDays(3))

        and:
        client()
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()

        and:
        TimeMachine.useDefaultClock()

        and:
        upsellWorkflowConsumer.consume()

        then:
        workflowService.findWorkflows(WorkflowQuery.byWorkflowName(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.WORKFLOW, WorkflowStatus.ACTIVE)).size() == 1
        workflowService.findWorkflows(WorkflowQuery.byWorkflowName(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.WORKFLOW, WorkflowStatus.TERMINATED)).size() == 0

        when:
        def settings = settingsService.getJson(AlfaSettings.APPLICATION_OF_UPSELL_SETTINGS, AlfaSettings.ApplicationOfUpsellSettings)

        and:
        settings.minDaysSinceLoanIssue = 4

        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.APPLICATION_OF_UPSELL_SETTINGS, textValue: JsonUtils.writeValueAsString(settings)))

        and:
        upsellWorkflowConsumer.consume()

        then:
        workflowService.findWorkflows(WorkflowQuery.byWorkflowName(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.WORKFLOW, WorkflowStatus.ACTIVE)).size() == 0
        workflowService.findWorkflows(WorkflowQuery.byWorkflowName(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.WORKFLOW, WorkflowStatus.TERMINATED)).size() == 1
    }

    def "Issue loan upsell"() {
        given:
        def client = client()

        when:
        def workflow = client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .issueUpsell()

        then:
        with(loanApplicationService.get(workflow.applicationId)) {
            type == LoanApplicationType.UPSELL
            status == LoanApplicationStatus.OPEN
            statusDetail == LoanApplicationStatusDetail.PENDING
            submittedAt
            requestedPrincipal == 500.00
            requestedPeriodUnit == PeriodUnit.DAY
            requestedPeriodCount == 30
            requestedInstallments == 0
            requestedInterestDiscountPercent == 25.00
            !offerDate
            offeredPrincipal == 0.00
            offeredInterest == 0.00
            offeredInterestDiscountPercent == 0.00
            offeredInterestDiscountAmount == 0.00
            offeredPeriodUnit == PeriodUnit.NA
            offeredPeriodCount == 0
            creditLimit == 600.00
            shortApproveCode
            longApproveCode
        }

        when:
        workflow
            .runAfterActivity(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.PREPARE_OFFER)

        then:
        with(loanApplicationService.get(workflow.applicationId)) {
            type == LoanApplicationType.UPSELL
            status == LoanApplicationStatus.OPEN
            statusDetail == LoanApplicationStatusDetail.PENDING
            submittedAt
            requestedPrincipal == 500.00
            requestedPeriodUnit == PeriodUnit.DAY
            requestedPeriodCount == 30
            requestedInstallments == 0
            requestedInterestDiscountPercent == 25.00
            offerDate
            offeredPrincipal == 500.00
            offeredInterest == 131.00
            offeredInterestDiscountPercent == 25.00
            offeredInterestDiscountAmount == 43.75
            offeredPeriodUnit == PeriodUnit.DAY
            offeredPeriodCount == 30
            creditLimit == 500.00
            shortApproveCode
            longApproveCode
        }

        when:
        workflow
            .runAll()
            .exportDisbursement()

        then:
        with(loanApplicationService.get(workflow.applicationId)) {
            type == LoanApplicationType.UPSELL
            status == LoanApplicationStatus.CLOSED
            statusDetail == LoanApplicationStatusDetail.APPROVED
            submittedAt
            requestedPrincipal == 500.00
            requestedPeriodUnit == PeriodUnit.DAY
            requestedPeriodCount == 30
            requestedInstallments == 0
            requestedInterestDiscountPercent == 25.00
            offerDate
            offeredPrincipal == 500.00
            offeredInterest == 131.00
            offeredInterestDiscountPercent == 25.00
            offeredInterestDiscountAmount == 43.75
            offeredPeriodUnit == PeriodUnit.DAY
            offeredPeriodCount == 30
            creditLimit == 500.00
            shortApproveCode
            longApproveCode
        }

        when:
        def balance = workflow
            .toLoan()
            .getBalance()

        then:
        with(balance) {
            totalDue == 126.00 + 631.00
            principalDue == 100.00 + 500.00
            interestDue == 26.00 + 131.00
        }
    }

    def "Issue loan upsell with discount used from initial loan application"() {
        given:
        def client = client()
        def applyDiscountCommand = new ApplyDiscountCommand(clientId: client.clientId, rateInPercent: 80.00, effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today().plusDays(15))

        when:
        def discount = discountService.applyDiscount(applyDiscountCommand)
        def underwritingWorkflow = client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()

        then: "Issued loan application with expected discount"
        with(loanApplicationService.get(underwritingWorkflow.applicationId)) {
            type == LoanApplicationType.NEW_LOAN
            status == LoanApplicationStatus.CLOSED
            statusDetail == LoanApplicationStatusDetail.APPROVED
            offeredInterestDiscountPercent == 80.00
            discountId == discount.id
        }

        when: "Issue upsell"
        def upsellWorkflow = underwritingWorkflow.exportDisbursement()
            .toLoan()
            .issueUpsell()
            .runAfterActivity(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.PREPARE_OFFER)

        then: "Issued Upsell loan application with same discount"
        with(loanApplicationService.get(upsellWorkflow.applicationId)) {
            type == LoanApplicationType.UPSELL
            status == LoanApplicationStatus.OPEN
            statusDetail == LoanApplicationStatusDetail.PENDING
            offeredInterestDiscountPercent == 80.00
            discountId == discount.id
        }
    }

    def "Reject loan upsell"() {
        given:
        def client = client()

        when:
        def workflow = client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .issueUpsell()
            .runAfterActivity(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.PREPARE_OFFER)

        and:
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL_OFFER_CALL).id))

        then:
        tasks.size() == 1

        with(tasks[0]) {
            taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.UpsellOfferCall.TYPE
            status == TaskStatus.OPEN
        }

        when:
        workflow.taskOfActivity(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL_OFFER_CALL).complete(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.UpsellOfferCall.CLIENT_REFUSED)

        then:
        workflow.isTerminated()

        with(loanApplicationService.get(workflow.applicationId)) {
            type == LoanApplicationType.UPSELL
            status == LoanApplicationStatus.CLOSED
            statusDetail == LoanApplicationStatusDetail.REJECTED
        }
    }

    def "Terminate upsell workflow on loan due date"() {
        given:
        def client = client()

        when:
        def workflow = client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .issueUpsell()
            .runAfterActivity(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.PREPARE_OFFER)

        and:
        TimeMachine.useFixedClockAt(TimeMachine.today().plusDays(30))

        and:
        loanService.resolveLoanDerivedValues(workflow.toLoan().loan.id, TimeMachine.today())

        and:
        def tasks = taskService.findTasks(TaskQuery.byWorkflowId(workflow.workflow.id))

        then:
        workflow.workflow.status == WorkflowStatus.TERMINATED

        and:
        tasks.size() == 1

        tasks[0].status == TaskStatus.CANCELLED
    }

    def "Terminate upsell workflow on loan closed"() {
        given:
        def client = client()

        when:
        def workflow = client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .issueUpsell()
            .runAfterActivity(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.PREPARE_OFFER)

        and:
        workflow.toLoan().repayAll(TimeMachine.today())

        and:
        def tasks = taskService.findTasks(TaskQuery.byWorkflowId(workflow.workflow.id))

        then:
        workflow.isTerminated()

        and:
        tasks.size() == 1

        tasks[0].status == TaskStatus.CANCELLED
    }

    def "Terminate upsell workflow and cancel application on offer call task expiration"() {
        given: "UpsellOfferCallTask expired in same day"
        def client = client()
        def setting = settingsService.getJson(AlfaSettings.APPLICATION_OF_UPSELL_SETTINGS, AlfaSettings.ApplicationOfUpsellSettings.class)
        setting.setMaxDaysUpsellOfferCallTaskActive(0)
        saveJsonSettings(AlfaSettings.APPLICATION_OF_UPSELL_SETTINGS, setting)

        when:
        def workflow = client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .issueUpsell()
            .runBeforeActivity(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL_OFFER_CALL)

        and: "Expire all tasks before now"
        expiredTaskConsumer.consume(TimeMachine.now())

        and: "Get task after expiration"
        def task = taskService.findTasks(TaskQuery.byWorkflowId(workflow.workflow.id)).find {
            it.taskType == fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL_OFFER_CALL
        }

        then:
        workflow.isExpired()
        workflow.workflow.terminateReason == fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL_OFFER_CALL + ": " + fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE
        workflow.toApplication().statusDetail == LoanApplicationStatusDetail.CANCELLED
        workflow.toApplication().closeReason == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.UpsellOfferCall.EXPIRE
        task.status == TaskStatus.COMPLETED
        task.resolution == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.UpsellOfferCall.EXPIRE

    }

    def "Issue loan Upsell approved by client update loan status details in expected statuses "() {
        given:
        def client = client()

        when:
        def workflow = client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .issueUpsell()

        and: "Issuing upsell transaction"
        workflow.runAfterActivity(fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL)

        then: "Loan status detail updated to DISBURSING_UPSELL"
        workflow.toLoan().statusDetail == LoanStatusDetail.DISBURSING_UPSELL

        when: "Upselled loan was disbursed"
        workflow.exportDisbursement()

        then: "Loan status detail updated to ACTIVE"
        workflow.toLoan().statusDetail == LoanStatusDetail.ACTIVE
    }

    def client() {
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setDateOfBirth(TimeMachine.today().minusYears(36))
            .setAmount(1000.00)
            .withCreditLimit(1000.00)
            .signUp()

        client
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        2.times {
            client
                .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
                .toLoanWorkflow()
                .runAll()
                .exportDisbursement()
                .toLoan()
                .repayAll(TimeMachine.today())
        }

        client
    }
}
