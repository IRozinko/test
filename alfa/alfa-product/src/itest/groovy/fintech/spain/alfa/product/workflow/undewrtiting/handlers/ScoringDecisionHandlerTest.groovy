package fintech.spain.alfa.product.workflow.undewrtiting.handlers

import fintech.lending.core.application.LoanApplicationService
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static fintech.lending.core.application.LoanApplicationStatusDetail.PENDING
import static fintech.lending.core.application.LoanApplicationStatusDetail.REJECTED
import static fintech.spain.alfa.product.workflow.WorkflowAttributes.SCORING_RATING
import static fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
import static fintech.spain.alfa.product.workflow.common.Resolutions.FAIL
import static fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.PREPARE_OFFER
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.SCORING_DECISION
import static fintech.workflow.ActivityStatus.COMPLETED
import static fintech.workflow.WorkflowStatus.ACTIVE
import static fintech.workflow.WorkflowStatus.TERMINATED

class ScoringDecisionHandlerTest extends AbstractAlfaTest {

    @Autowired
    LoanApplicationService loanApplicationService

    @Unroll
    def "ScoringDecision: #rating, #wf_status, #resolution, #status"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def wf = client.toLoanWorkflow()

        when:
        wf.runBeforeActivity(SCORING_DECISION)

        if (rating != null)
            wf.setAttribute(SCORING_RATING, rating)
        else
            wf.removeAttribute(SCORING_RATING)

        wf.runBeforeActivity(PREPARE_OFFER)

        then:
        wf.getActivity(SCORING_DECISION).resolution == resolution
        wf.getActivity(SCORING_DECISION).status == status
        wf.getWorkflow().status == wf_status
        loanApplicationService.get(wf.getApplicationId()).statusDetail == app_status_detail

        where:
        rating       | wf_status  | resolution | status    | app_status_detail
        "A100000000" | ACTIVE     | APPROVE    | COMPLETED | PENDING
        "R100000000" | TERMINATED | REJECT     | COMPLETED | REJECTED
        "E1"         | TERMINATED | FAIL       | COMPLETED | REJECTED
        null         | TERMINATED | FAIL       | COMPLETED | REJECTED
        "JAMES"      | TERMINATED | FAIL       | COMPLETED | REJECTED

    }

}
