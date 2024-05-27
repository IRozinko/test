package fintech.spain.alfa.product

import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.lending.core.loan.LoanStatusDetail
import fintech.risk.checklist.CheckListConstants
import fintech.risk.checklist.commands.AddCheckListEntryCommand
import fintech.spain.alfa.product.cms.CmsSetup
import fintech.workflow.ActivityStatus
import spock.lang.Ignore

import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.MANDATORY_LENDING_RULES
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.PHONE_VERIFICATION

class FirstLoanWorkflowTest extends AbstractAlfaTest {

    def "first loan issued with instantor"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then:
        workflow.print()
        workflow.isActive()
        workflow.toLoan().getStatusDetail() == LoanStatusDetail.DISBURSING

        when:
        workflow.exportDisbursement()

        then:
        workflow.print()
        workflow.isCompleted()
        workflow.toLoan().getStatusDetail() == LoanStatusDetail.ACTIVE
    }

    @Ignore
    def "first loan issued with document upload"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .setDocumentUpload(true)
            .runAll()

        then:
        workflow.print()
        workflow.isActive()
        workflow.toLoan().getStatusDetail() == LoanStatusDetail.DISBURSING

        when:
        workflow.exportDisbursement()

        then:
        workflow.print()
        workflow.isCompleted()
        workflow.toLoan().getStatusDetail() == LoanStatusDetail.ACTIVE
    }

    def "phone verification"() {
        given:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runBeforeActivity(PHONE_VERIFICATION)

        expect:
        workflow.isActivityActive(PHONE_VERIFICATION)
        workflow.toClient().smsCount(CmsSetup.PHONE_VERIFICATION_NOTIFICATION) == 1

        when:
        workflow.toClient().verifyPhone("invalid")

        then:
        workflow.isActivityActive(PHONE_VERIFICATION)

        when:
        workflow.toClient().verifyPhone()

        then:
        workflow.isActivityCompleted(PHONE_VERIFICATION)
    }

    def "first loan not issued because client's dni is in black list"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_DNI, value1: client.dni))
        def workflow = client.toLoanWorkflow()
            .runAll()

        then:
        workflow.print()
        !workflow.isActive()
        workflow.getActivityStatus(MANDATORY_LENDING_RULES) == ActivityStatus.COMPLETED
        workflow.getActivityResolution(MANDATORY_LENDING_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
        workflow.getActivityResolutionDetail(MANDATORY_LENDING_RULES) == AlfaConstants.REJECT_REASON_DNI_NOT_ALLOWED
        workflow.toApplication().getStatusDetail() == LoanApplicationStatusDetail.REJECTED
    }

    def "first loan not issued because client's email is in black list"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_EMAIL, value1: client.email))
        def workflow = client.toLoanWorkflow()
            .runAll()

        then:
        workflow.print()
        !workflow.isActive()
        workflow.getActivityStatus(MANDATORY_LENDING_RULES) == ActivityStatus.COMPLETED
        workflow.getActivityResolution(MANDATORY_LENDING_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
        workflow.getActivityResolutionDetail(MANDATORY_LENDING_RULES) == AlfaConstants.REJECT_REASON_EMAIL_NOT_ALLOWED
        workflow.toApplication().getStatusDetail() == LoanApplicationStatusDetail.REJECTED
    }

    def "first loan not issued because client's phone is in black list"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_PHONE, value1: client.mobilePhone))
        def workflow = client.toLoanWorkflow()
            .runAll()

        then:
        workflow.print()
        !workflow.isActive()
        workflow.getActivityStatus(MANDATORY_LENDING_RULES) == ActivityStatus.COMPLETED
        workflow.getActivityResolution(MANDATORY_LENDING_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
        workflow.getActivityResolutionDetail(MANDATORY_LENDING_RULES) == AlfaConstants.REJECT_REASON_PHONE_NOT_ALLOWED
        workflow.toApplication().getStatusDetail() == LoanApplicationStatusDetail.REJECTED
    }
}
