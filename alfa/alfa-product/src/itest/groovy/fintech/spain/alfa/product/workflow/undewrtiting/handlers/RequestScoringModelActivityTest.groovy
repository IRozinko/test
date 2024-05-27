package fintech.spain.alfa.product.workflow.undewrtiting.handlers

import fintech.JsonUtils
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.scoring.model.ScoringModelType
import fintech.spain.scoring.model.ScoringQuery
import fintech.spain.scoring.model.ScoringRequestStatus
import fintech.spain.scoring.model.ScoringResult
import fintech.workflow.ActivityStatus
import fintech.workflow.WorkflowStatus

import static fintech.spain.alfa.product.scoring.ScoringRequestAttributes.APPLICATION_ID
import static fintech.spain.alfa.product.scoring.ScoringRequestAttributes.CLIENT_ID
import static fintech.spain.alfa.product.scoring.ScoringRequestAttributes.WORKFLOW_ACTIVITY
import static fintech.spain.alfa.product.scoring.ScoringRequestAttributes.WORKFLOW_ID
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.REQUEST_SCORE

class RequestScoringModelActivityTest extends AbstractAlfaTest {

    def "Workflow terminated after RequestScoringModelActivity failed"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def wf = client.toLoanWorkflow()

        when:
        wf.runBeforeActivity(REQUEST_SCORE)
        wf.failSystemActivity(REQUEST_SCORE)

        then:
        wf.getWorkflow().status == WorkflowStatus.TERMINATED
        wf.getActivity(REQUEST_SCORE).status == ActivityStatus.FAILED
    }

    def "RequestScoringModelActivity successfully completed"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def wf = client.toLoanWorkflow()

        when:
        wf.runAfterActivity(REQUEST_SCORE)

        then:
        Optional<ScoringResult> result = scoringService.findLatest(new ScoringQuery(type: ScoringModelType.FINTECH_MARKET,
            clientId: client.getClientId(),
            applicationId: client.getApplicationId(),
            statuses: [ScoringRequestStatus.OK]))
        result.isPresent()
        with(result.get()) {
            !it.requestAttributes.isEmpty()
            def attributes = JsonUtils.readValueAsMap(it.requestAttributes)
            attributes[APPLICATION_ID] == client.applicationId
            attributes[CLIENT_ID] == client.getClientId()
            attributes[WORKFLOW_ID] == wf.getWorkflowId()
            attributes[WORKFLOW_ACTIVITY] == REQUEST_SCORE
        }
        wf.getWorkflow().status == WorkflowStatus.ACTIVE
        wf.getActivity(REQUEST_SCORE).status == ActivityStatus.COMPLETED
    }

}
