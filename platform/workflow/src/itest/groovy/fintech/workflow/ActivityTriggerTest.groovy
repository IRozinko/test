package fintech.workflow

import fintech.workflow.db.TriggerRepository
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime

class ActivityTriggerTest extends WorkflowSpecification {

    @Autowired
    TriggerService triggerService

    @Autowired
    TriggerRepository triggerRepository

    def "Cancel trigger on activity complete"() {
        given:
        startWorkflow(builder()
            .agentActivity("1")
            .resolutions("ok")
            .add()
            .agentActivity("2")
            .resolutions("ok")
            .add())

        def triggerId = triggerService.addTrigger(new AddTriggerCommand(activityId: workflow.activity("1").id, name: "test", nextAttemptAt: LocalDateTime.now()))

        when:
        workflowService.completeActivity(workflow.activity("1").id, "ok", "")

        then:
        workflow.status == WorkflowStatus.ACTIVE
        workflow.activity("1").status == ActivityStatus.COMPLETED

        with(triggerRepository.getRequired(triggerId)) {
            status == TriggerStatus.CANCELLED
        }
    }

    def "Cancel trigger on workflow complete"() {
        given:
        startWorkflow(builder()
            .agentActivity("1")
            .resolutions("ok")
            .completeWorkflowOnResolutions("ok")
            .add()
            .agentActivity("2")
            .resolutions("ok")
            .add())

        def triggerId = triggerService.addTrigger(new AddTriggerCommand(activityId: workflow.activity("2").id, name: "test", nextAttemptAt: LocalDateTime.now()))

        when:
        workflowService.completeActivity(workflow.activity("1").id, "ok", "")

        then:
        workflow.status == WorkflowStatus.COMPLETED
        workflow.activity("1").status == ActivityStatus.COMPLETED

        with(triggerRepository.getRequired(triggerId)) {
            status == TriggerStatus.CANCELLED
        }
    }

    def "Cancel trigger on workflow terminate"() {
        given:
        startWorkflow(builder()
            .agentActivity("1")
            .resolutions("ok")
            .terminateWorkflowOnResolutions("ok")
            .add()
            .agentActivity("2")
            .resolutions("ok")
            .add())

        def triggerId = triggerService.addTrigger(new AddTriggerCommand(activityId: workflow.activity("2").id, name: "test", nextAttemptAt: LocalDateTime.now()))

        when:
        workflowService.completeActivity(workflow.activity("1").id, "ok", "")

        then:
        workflow.status == WorkflowStatus.TERMINATED
        workflow.activity("1").status == ActivityStatus.COMPLETED

        with(triggerRepository.getRequired(triggerId)) {
            status == TriggerStatus.CANCELLED
        }
    }
}
