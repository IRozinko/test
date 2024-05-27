package fintech.spain.alfa.product

import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.workflow.ActivityStatus
import spock.lang.Unroll

class LoanOfferCallTest extends AbstractAlfaTest {

    @Unroll
    def "manual task resolution: #taskResolution"() {
        given:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER)

        expect:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER) == ActivityStatus.ACTIVE

        when:
        workflow.taskOfActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER).complete(taskResolution)

        then:
        assert workflow.toApplication().getStatusDetail() == applicationStatusDetail

        where:
        taskResolution                                        | applicationStatusDetail
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.LoanOfferCall.CLIENT_APPROVED_OFFER | LoanApplicationStatusDetail.PENDING
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.LoanOfferCall.CLIENT_REJECTED_OFFER | LoanApplicationStatusDetail.CANCELLED
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.LoanOfferCall.EXPIRE                | LoanApplicationStatusDetail.CANCELLED
    }
}
