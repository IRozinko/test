package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.risk.checklist.CheckListConstants
import fintech.risk.checklist.commands.AddCheckListEntryCommand
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.workflow.ActivityStatus

import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.EQUIFAX_RULES_RUN_2
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.EQUIFAX_RUN_2
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.EXPERIAN_CAIS_OPERACIONES_RUN_2
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.EXPERIAN_CAIS_RESUMEN_RUN_2
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.EXPERIAN_RULES_RUN_1
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.EXPERIAN_RULES_RUN_2
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.PREPARE_OFFER
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.UNRESPONSIVE_BUREAU_RULE

class UnresponsiveBureauRuleActivityTest extends AbstractAlfaTest {

    AlfaSettings.UnresponsiveBureauSettings settings

    def setup() {
        settings = settingsService.getJson(AlfaSettings.UNRESPONSIVE_BUREAU_SETTINGS, AlfaSettings.UnresponsiveBureauSettings.class)
    }

    def "In case of Equifax and Experian rules with success -> Unresponsive bureau is auto-completed with CANCEL"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()

        when:
        def workflow = client.toLoanWorkflow().runAll().exportDisbursement()

        then:
        workflow.isCompleted()
        workflow.getActivity(UNRESPONSIVE_BUREAU_RULE).status == ActivityStatus.CANCELLED
    }

    def "In case of Equifax response failed and Experian fail-> Unresponsive bureau approved, client has paid loans in last days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        def issueFirstLoanDate = TimeMachine.today().minusDays(10)
        def loan = client.issueActiveLoan(300.00, 30, issueFirstLoanDate).repayAll(issueFirstLoanDate)

        then:
        loan.isPaid()

        when:
        settings.setMaxDaysSinceLastLoanPaid(10)
        saveJsonSettings(AlfaSettings.UNRESPONSIVE_BUREAU_SETTINGS, settings)
        mockEquifaxProvider.throwError = true
        mockExperianCaisProvider.throwError = true

        def workflow = client.submitApplicationAndStartFirstLoanWorkflow(200.00, 30, TimeMachine.today()).toLoanWorkflow().runAll().exportDisbursement(TimeMachine.today())

        then:
        workflow.isCompleted()
        workflow.getActivityResolution(UNRESPONSIVE_BUREAU_RULE) == fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
        workflow.getActivityResolution(PREPARE_OFFER) == fintech.spain.alfa.product.workflow.common.Resolutions.OK
        workflow.areAllActivitiesCompleted(EQUIFAX_RUN_2, UNRESPONSIVE_BUREAU_RULE)
        workflow.areAllActivitiesCancelled(EQUIFAX_RULES_RUN_2, EXPERIAN_CAIS_RESUMEN_RUN_2, EXPERIAN_CAIS_OPERACIONES_RUN_2, EXPERIAN_RULES_RUN_2, PREPARE_OFFER)
    }

    def "In case of Equifax response failed and Experian success-> Unresponsive bureau still run and approve"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        def issueFirstLoanDate = TimeMachine.today().minusDays(10)
        def loan = client.issueActiveLoan(300.00, 30, issueFirstLoanDate).repayAll(issueFirstLoanDate)

        then:
        loan.isPaid()

        when:
        settings.setMaxDaysSinceLastLoanPaid(10)
        saveJsonSettings(AlfaSettings.UNRESPONSIVE_BUREAU_SETTINGS, settings)
        mockEquifaxProvider.throwError = true

        def workflow = client.submitApplicationAndStartFirstLoanWorkflow(200.00, 30, TimeMachine.today()).toLoanWorkflow().runAll().exportDisbursement(TimeMachine.today())

        then:
        workflow.isCompleted()
        workflow.getActivityResolution(UNRESPONSIVE_BUREAU_RULE) == fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
        workflow.getActivityResolution(PREPARE_OFFER) == fintech.spain.alfa.product.workflow.common.Resolutions.OK
    }

    def "In case of Equifax response failed and Experian success-> Unresponsive bureau still run and reject, client has not paid loans in last days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        def issueFirstLoanDate = TimeMachine.today().minusDays(10)
        def loan = client.issueActiveLoan(300.00, 30, issueFirstLoanDate).repayAll(issueFirstLoanDate)

        then:
        loan.isPaid()

        when:
        settings.setMaxDaysSinceLastLoanPaid(9)
        saveJsonSettings(AlfaSettings.UNRESPONSIVE_BUREAU_SETTINGS, settings)
        mockEquifaxProvider.throwError = true

        def workflow = client.submitApplicationAndStartFirstLoanWorkflow(200.00, 30, TimeMachine.today()).toLoanWorkflow().runAll()

        then:
        workflow.isTerminated()
        workflow.getActivityResolution(UNRESPONSIVE_BUREAU_RULE) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
        workflow.areAllActivitiesCompleted(EQUIFAX_RUN_2, UNRESPONSIVE_BUREAU_RULE)
        workflow.areAllActivitiesCancelled(EQUIFAX_RULES_RUN_2, EXPERIAN_CAIS_RESUMEN_RUN_2, EXPERIAN_CAIS_OPERACIONES_RUN_2, EXPERIAN_RULES_RUN_2, PREPARE_OFFER)
    }

    def "In case of Equifax response failed and Experian success-> PREPARE_OFFER not activated"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        def issueFirstLoanDate = TimeMachine.today().minusDays(10)
        def loan = client.issueActiveLoan(300.00, 30, issueFirstLoanDate).repayAll(issueFirstLoanDate)

        then:
        loan.isPaid()

        when:
        settings.setMaxDaysSinceLastLoanPaid(9)
        saveJsonSettings(AlfaSettings.UNRESPONSIVE_BUREAU_SETTINGS, settings)
        mockEquifaxProvider.throwError = true

        def workflow = client.submitApplicationAndStartFirstLoanWorkflow(200.00, 30, TimeMachine.today()).toLoanWorkflow()
        workflow.runBeforeActivity(UNRESPONSIVE_BUREAU_RULE)
        workflow.runSystemActivity(PREPARE_OFFER)

        then:
        workflow.getActivityStatus(PREPARE_OFFER) == ActivityStatus.WAITING
    }

    def "In case of Equifax response failed and Experian rejected by un-allowed province-> Unresponsive bureau cancelled and workflow rejected"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        settings.setMaxDaysSinceLastLoanPaid(10)
        saveJsonSettings(AlfaSettings.UNRESPONSIVE_BUREAU_SETTINGS, settings)
        mockEquifaxProvider.throwError = true
        mockExperianCaisProvider.throwError = true

        def workflow = client.signUp().toLoanWorkflow().runAfterActivity(EXPERIAN_RULES_RUN_1)

        mockExperianCaisProvider.throwError = false
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_PROVINCE_CODE, value1: "35"))
        mockExperianCaisProvider.resumenResponseResource = "experian-cais-resumen-province-code-35.xml"

        workflow.runAll()

        then:
        workflow.isTerminated()
        workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_PROVINCE_NOT_ALLOWED
        workflow.getActivityResolution(EXPERIAN_RULES_RUN_2) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
        workflow.getActivityStatus(UNRESPONSIVE_BUREAU_RULE) == ActivityStatus.CANCELLED
        workflow.getActivityStatus(PREPARE_OFFER) == ActivityStatus.CANCELLED
    }

    def "In case of Equifax response failed and Experian fails-> Unresponsive bureau rejected, client has not paid loans in last days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        def issueFirstLoanDate = TimeMachine.today().minusDays(10)
        def loan = client.issueActiveLoan(300.00, 30, issueFirstLoanDate).repayAll(issueFirstLoanDate)

        then:
        loan.isPaid()

        when:
        settings.setMaxDaysSinceLastLoanPaid(9)
        saveJsonSettings(AlfaSettings.UNRESPONSIVE_BUREAU_SETTINGS, settings)
        mockEquifaxProvider.throwError = true
        mockExperianCaisProvider.throwError = true

        def workflow = client.submitApplicationAndStartFirstLoanWorkflow(200.00, 30, TimeMachine.today()).toLoanWorkflow().runAll()

        then:
        workflow.isTerminated()
        workflow.getActivityResolution(UNRESPONSIVE_BUREAU_RULE) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
        workflow.areAllActivitiesCompleted(EQUIFAX_RUN_2, UNRESPONSIVE_BUREAU_RULE)
        workflow.areAllActivitiesCancelled(EQUIFAX_RULES_RUN_2, EXPERIAN_CAIS_RESUMEN_RUN_2, EXPERIAN_CAIS_OPERACIONES_RUN_2, EXPERIAN_RULES_RUN_2, PREPARE_OFFER)
    }

    def "In case of Experian response failed -> Unresponsive bureau approved, client has paid loans in last days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        def issueFirstLoanDate = TimeMachine.today().minusDays(10)
        def loan = client.issueActiveLoan(300.00, 30, issueFirstLoanDate).repayAll(issueFirstLoanDate)

        then:
        loan.isPaid()

        when:
        settings.setMaxDaysSinceLastLoanPaid(10)
        saveJsonSettings(AlfaSettings.UNRESPONSIVE_BUREAU_SETTINGS, settings)
        mockExperianCaisProvider.throwError = true

        def workflow = client.submitApplicationAndStartFirstLoanWorkflow(200.00, 30, TimeMachine.today()).toLoanWorkflow().runAll().exportDisbursement(TimeMachine.today())

        then:
        workflow.isCompleted()
        workflow.getActivityResolution(UNRESPONSIVE_BUREAU_RULE) == fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
        workflow.getActivityResolution(PREPARE_OFFER) == fintech.spain.alfa.product.workflow.common.Resolutions.OK
        workflow.areAllActivitiesCompleted(EQUIFAX_RUN_2, EQUIFAX_RULES_RUN_2, EXPERIAN_CAIS_RESUMEN_RUN_2, UNRESPONSIVE_BUREAU_RULE)
        workflow.areAllActivitiesCancelled(EXPERIAN_CAIS_OPERACIONES_RUN_2, EXPERIAN_RULES_RUN_2, PREPARE_OFFER)
    }

    def "In case of Experian response failed -> Unresponsive bureau rejected, client has not paid loans in last days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        def issueFirstLoanDate = TimeMachine.today().minusDays(10)
        def loan = client.issueActiveLoan(300.00, 30, issueFirstLoanDate).repayAll(issueFirstLoanDate)

        then:
        loan.isPaid()

        when:
        settings.setMaxDaysSinceLastLoanPaid(9)
        saveJsonSettings(AlfaSettings.UNRESPONSIVE_BUREAU_SETTINGS, settings)
        mockExperianCaisProvider.throwError = true

        def workflow = client.submitApplicationAndStartFirstLoanWorkflow(200.00, 30, TimeMachine.today()).toLoanWorkflow().runAll()

        then:
        workflow.isTerminated()
        workflow.getActivityResolution(UNRESPONSIVE_BUREAU_RULE) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
        workflow.areAllActivitiesCompleted(EQUIFAX_RUN_2, EQUIFAX_RULES_RUN_2, EXPERIAN_CAIS_RESUMEN_RUN_2, UNRESPONSIVE_BUREAU_RULE)
        workflow.areAllActivitiesCancelled(EXPERIAN_CAIS_OPERACIONES_RUN_2, EXPERIAN_RULES_RUN_2, PREPARE_OFFER)
    }

    def "In case of Unresponsive bureau rejected -> the loan application is cancelled"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        when:
        def issueFirstLoanDate = TimeMachine.today().minusDays(10)
        def loan = client.issueActiveLoan(300.00, 30, issueFirstLoanDate).repayAll(issueFirstLoanDate)

        then:
        loan.isPaid()

        when:
        settings.setMaxDaysSinceLastLoanPaid(9)
        saveJsonSettings(AlfaSettings.UNRESPONSIVE_BUREAU_SETTINGS, settings)
        mockExperianCaisProvider.throwError = true

        def workflow = client.submitApplicationAndStartFirstLoanWorkflow(200.00, 30, TimeMachine.today()).toLoanWorkflow().runAll()

        then:
        workflow.isTerminated()
        workflow.getActivityResolution(UNRESPONSIVE_BUREAU_RULE) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
        workflow.toApplication().getStatusDetail() == LoanApplicationStatusDetail.CANCELLED
    }
}
