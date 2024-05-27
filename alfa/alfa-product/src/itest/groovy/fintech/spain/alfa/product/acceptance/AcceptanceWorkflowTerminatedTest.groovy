package fintech.spain.alfa.product.acceptance

import fintech.lending.core.application.LoanApplicationStatus
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail
import fintech.payments.DisbursementService
import fintech.payments.model.DisbursementStatus
import fintech.payments.model.DisbursementStatusDetail
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.workflow.common.Attributes
import org.springframework.beans.factory.annotation.Autowired

import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.WAITING_EXPORT_DISBURSEMENT

class AcceptanceWorkflowTerminatedTest extends AbstractAlfaTest {

    @Autowired
    DisbursementService disbursementService

    def "when workflow terminated during waiting export disbursement - loan is voided and application is cancelled"() {
        given:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .runBeforeActivity(WAITING_EXPORT_DISBURSEMENT)

        when:
        workflow.terminate("Testing cancellation")

        then:
        workflow.print()
        workflow.isTerminated()

        and:
        with(workflow.toLoan()) {
            statusDetail == LoanStatusDetail.VOIDED
            status == LoanStatus.CLOSED
        }

        and:
        with(workflow.toApplication().getApplication()) {
            statusDetail == LoanApplicationStatusDetail.CANCELLED
            status == LoanApplicationStatus.CLOSED
        }

        and:
        Long disbursementId = workflow.getAttribute(Attributes.DISBURSEMENT_ID)
            .map({ Long.valueOf(it) })
            .orElseThrow({ new IllegalStateException("") })
        with(disbursementService.getDisbursement(disbursementId)) {
            statusDetail == DisbursementStatusDetail.CANCELLED
            status == DisbursementStatus.CLOSED
        }
    }
}
