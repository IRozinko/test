package fintech.workflow

import fintech.workflow.db.WorkflowRepository
import fintech.workflow.impl.WorkflowBackgroundJobs
import fintech.workflow.spi.ActivityTrigger
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime

import static fintech.workflow.spi.WorkflowBuilder.completed

class WorkflowTest extends WorkflowSpecification {


    @Autowired
    WorkflowBackgroundJobs systemActorScheduler

    @Autowired
    WorkflowRepository workflowRepository

    def "Workflow is completed on activity resolution"() {
        given:
        startWorkflow(builder()
            .addOnCompletedListener(MockWorkflowListener.class)
            .agentActivity("activity")
            .resolutions("ok")
            .completeWorkflowOnResolutions("ok")
            .add())

        when:
        workflowService.completeActivity(workflow.activity("activity").id, "ok", "")

        then:
        MockWorkflowListener.executed == 1
        getWorkflow().status == WorkflowStatus.COMPLETED
    }

    def "Workflow is terminated on activity resolution"() {
        given:
        startWorkflow(builder()
            .clientActivity("activity")
            .resolutions("ok")
            .terminateWorkflowOnResolutions("ok")
            .add())

        when:
        workflowService.completeActivity(workflow.activity("activity").id, "ok", "")

        then:
        getWorkflow().status == WorkflowStatus.TERMINATED
    }

    def "Activity handler and listeners are executed"() {
        when:
        activityHandlersAndListenersWf()

        then:
        MockActivityListener.executed == 1 // on started

        when:
        systemActorScheduler.run(LocalDateTime.now())
        workflow = getWorkflow()

        then:
        MockActivityHandler.executed == 1
        MockActivityListener.executed == 2 // on started and on completed
    }

    def "No activities executed when workflow is suspended"() {
        when:
        def id = activityHandlersAndListenersWf().id

        then:
        MockActivityHandler.executed == 0

        when:
        workflowService.suspend(id)
        systemActorScheduler.run(LocalDateTime.now())

        then:
        MockActivityHandler.executed == 0

        when:
        workflowService.resume(id)
        systemActorScheduler.run(LocalDateTime.now())

        then:
        MockActivityHandler.executed == 1
    }

    def "Activity expired"() {
        given:
        startWorkflow(builder()
            .clientActivity("activity")
            .resolutions("ok", "expired")
            .expires(60, "expired")
            .onResolution("expired", MockActivityListener.class)
            .completeWorkflowOnResolutions("ok")
            .expireWorkflowOnResolutions("expired")
            .add())

        when:
        systemActorScheduler.run(LocalDateTime.now())

        then: "not yet expired"
        getWorkflow().activity("activity").status == ActivityStatus.ACTIVE

        when:
        systemActorScheduler.run(LocalDateTime.now().plusSeconds(120))

        then: "expired"
        getWorkflow().activity("activity").status == ActivityStatus.COMPLETED
        getWorkflow().activity("activity").resolution == "expired"
        getWorkflow().status == WorkflowStatus.EXPIRED
        MockActivityListener.executed == 1
    }

    private Workflow activityHandlersAndListenersWf() {
        startWorkflow(builder()
            .systemActivity("activity")
            .resolutions("ok")
            .onStarted(MockActivityListener.class)
            .handler(MockActivityHandler.class)
            .onResolution("ok", MockActivityListener.class)
            .completeWorkflowOnResolutions("ok")
            .add())
    }

    def "Wait for any"() {
        when:
        startWorkflow(builder()
            .agentActivity("A").resolutions("ok").add()
            .agentActivity("B").resolutions("ok").add()
            .agentActivity("C").resolutions("ok")
            .waitForAny(completed("A", "ok"), completed("B", "ok"))
            .completeWorkflowOnResolutions("ok")
            .add())

        then:
        workflow.activity("A").status == ActivityStatus.ACTIVE
        workflow.activity("B").status == ActivityStatus.ACTIVE
        workflow.activity("C").status == ActivityStatus.WAITING

        when:
        workflowService.completeActivity(workflow.activity("A").id, "ok", "")
        workflow = getWorkflow()

        then:
        workflow.status == WorkflowStatus.ACTIVE
        workflow.activity("A").status == ActivityStatus.COMPLETED
        workflow.activity("B").status == ActivityStatus.ACTIVE
        workflow.activity("C").status == ActivityStatus.ACTIVE

        when:
        workflowService.completeActivity(workflow.activity("C").id, "ok", "")
        workflow = getWorkflow()

        then:
        workflow.status == WorkflowStatus.COMPLETED
        workflow.activity("A").status == ActivityStatus.COMPLETED
        workflow.activity("B").status == ActivityStatus.CANCELLED
        workflow.activity("C").status == ActivityStatus.COMPLETED
    }


    def "Check dynamic activity listeners"() {
        when:
        startWorkflow(builder()
            .agentActivity("A").resolutions("ok").onStartedDynamic(MOCK_TRIGGER_HANDLER_NAME, "param1").add()
            .agentActivity("B").resolutions("ok").onResolutionDynamic("ok", MOCK_TRIGGER_HANDLER_NAME, "param2").add()
            .agentActivity("C").resolutions("ok")
            .waitForAll(completed("A", "ok"), completed("B", "ok"))
            .completeWorkflowOnResolutions("ok")
            .add())

        then:
        with(activityListenerRepository.findExistedOnActivityStarted(workflow.name, workflow.version, "A")[0]) {
            params == ["param1"].toArray()
            workflowName == workflow.name
            workflowVersion == workflow.version
            activityName == "A"
            activityStatus == ActivityListenerStatus.STARTED
            !delaySec
            !resolution
        }

        with(activityListenerRepository.findExistedOnActivityCompleted(workflow.name, workflow.version, "B", "ok")[0]) {
            params == ["param2"].toArray()
            workflowName == workflow.name
            workflowVersion == workflow.version
            activityName == "B"
            activityStatus == ActivityListenerStatus.COMPLETED
            !delaySec
            resolution == "ok"
        }

        when:
        workflowService.completeActivity(workflow.activity("A").id, "ok", "")

        then:
        MockTriggerHandler.executed == 1

        when:
        workflowService.completeActivity(workflow.activity("B").id, "ok", "")

        then:
        MockTriggerHandler.executed == 2

        when:
        workflowService.completeActivity(workflow.activity("C").id, "ok", "")

        then:
        MockTriggerHandler.executed == 2
        workflow.status == WorkflowStatus.COMPLETED

    }

    def "Wait for all"() {
        when:
        startWorkflow(builder()
            .agentActivity("A").resolutions("ok").add()
            .agentActivity("B").resolutions("ok").add()
            .agentActivity("C").resolutions("ok")
            .waitForAll(completed("A", "ok"), completed("B", "ok"))
            .completeWorkflowOnResolutions("ok")
            .add())

        then:
        workflow.activity("A").status == ActivityStatus.ACTIVE
        workflow.activity("B").status == ActivityStatus.ACTIVE
        workflow.activity("C").status == ActivityStatus.WAITING

        when:
        workflowService.completeActivity(workflow.activity("A").id, "ok", "")
        workflow = getWorkflow()

        then:
        workflow.activity("A").status == ActivityStatus.COMPLETED
        workflow.activity("B").status == ActivityStatus.ACTIVE
        workflow.activity("C").status == ActivityStatus.WAITING

        when:
        workflowService.completeActivity(workflow.activity("B").id, "ok", "")
        workflow = getWorkflow()

        then:
        workflow.activity("A").status == ActivityStatus.COMPLETED
        workflow.activity("B").status == ActivityStatus.COMPLETED
        workflow.activity("C").status == ActivityStatus.ACTIVE

        when:
        workflowService.completeActivity(workflow.activity("C").id, "ok", "")
        workflow = getWorkflow()

        then:
        workflow.status == WorkflowStatus.COMPLETED
        workflow.activity("A").status == ActivityStatus.COMPLETED
        workflow.activity("B").status == ActivityStatus.COMPLETED
        workflow.activity("C").status == ActivityStatus.COMPLETED
    }

    def "Wait for all with multiple possible resolution"() {
        given:
        startWorkflow(builder()
            .agentActivity("A").resolutions("ok", "fail").add()
            .agentActivity("B").resolutions("ok", "fail").add()
            .agentActivity("C").resolutions("ok")
            .waitForAll(completed("A", "ok", "fail"), completed("B", "ok", "fail")).add()
            .agentActivity("D").resolutions("ok")
            .waitForAll(completed("C", "ok"))
            .completeWorkflowOnResolutions("ok")
            .add())

        when:
        workflowService.completeActivity(workflow.activity("A").id, "fail", "")

        then:
        getWorkflow().activity("C").status == ActivityStatus.WAITING

        when:
        workflowService.completeActivity(workflow.activity("B").id, "fail", "")

        then:
        getWorkflow().activity("C").status == ActivityStatus.ACTIVE

        when:
        workflowService.completeActivity(workflow.activity("C").id, "ok", "")

        then:
        getWorkflow().activity("D").status == ActivityStatus.ACTIVE

        when:
        workflowService.completeActivity(workflow.activity("D").id, "ok", "")

        then:
        getWorkflow().activity("D").status == ActivityStatus.COMPLETED
    }

    def "Wait for any with multiple possible resolution"() {
        given:
        startWorkflow(builder()
            .agentActivity("A").resolutions("ok", "fail").add()
            .agentActivity("B").resolutions("ok", "fail").add()
            .agentActivity("C").resolutions("ok")
            .waitForAny(completed("A", "ok", "fail"), completed("B", "ok", "fail")).add()
            .agentActivity("D").resolutions("ok")
            .waitForAll(completed("C", "ok"))
            .completeWorkflowOnResolutions("ok")
            .add())

        when:
        workflowService.completeActivity(workflow.activity("A").id, "fail", "")

        then:
        getWorkflow().activity("C").status == ActivityStatus.ACTIVE

        when:
        workflowService.completeActivity(workflow.activity("B").id, "fail", "")

        then:
        getWorkflow().activity("C").status == ActivityStatus.ACTIVE

        when:
        workflowService.completeActivity(workflow.activity("C").id, "ok", "")

        then:
        getWorkflow().activity("D").status == ActivityStatus.ACTIVE

        when:
        workflowService.completeActivity(workflow.activity("D").id, "ok", "")

        then:
        getWorkflow().activity("D").status == ActivityStatus.COMPLETED
    }

    def "Finders"() {
        given:
        startWorkflow(builder()
            .clientActivity("activity")
            .resolutions("ok")
            .add())

        expect:
        workflowService.findWorkflows(WorkflowQuery.byClientId(CLIENT_ID, workflow.getName(), WorkflowStatus.COMPLETED)).isEmpty()
        workflowService.findWorkflows(WorkflowQuery.byClientId(CLIENT_ID, workflow.getName(), WorkflowStatus.ACTIVE)).size() == 1
        workflowService.findWorkflows(WorkflowQuery.byClientId(CLIENT_ID, workflow.getName())).size() == 1
        !workflowService.findActivity(CLIENT_ID, workflow.getName(), "activity", ActivityStatus.COMPLETED).isPresent()
        workflowService.findActivity(CLIENT_ID, workflow.getName(), "activity", ActivityStatus.ACTIVE).isPresent()
        workflowService.findActivity(CLIENT_ID, workflow.getName(), "activity").isPresent()
    }

    def "Set attribute"() {
        when:
        startWorkflow(builder()
            .clientActivity("activity")
            .resolutions("ok")
            .add())

        then:
        !getWorkflow().hasAttribute("test")

        when:
        workflowService.setAttribute(workflowId, "test", "value")
        workflowService.setAttribute(workflowId, "test", "value2")
        workflowService.setAttribute(workflowId, "test2", "value3")

        then:
        getWorkflow().attribute("test") == "value2"

        when:
        workflowService.removeAttribute(workflowId, "test")

        then:
        !getWorkflow().hasAttribute("test")
        getWorkflow().hasAttribute("test2")
    }

    def "Retry and fail activity on exception"() {
        given:
        startWorkflow(builder()
            .systemActivity("activity")
            .resolutions("ok")
            .handler(MockExceptionActivityHandler.class)
            .maxAttempts(2)
            .attemptTimeoutInSeconds(5)
            .terminateWorkflowOnFailure(true)
            .completeWorkflowOnResolutions("ok")
            .add())

        when:
        systemActorScheduler.run(LocalDateTime.now())
        workflow = getWorkflow()

        then:
        MockExceptionActivityHandler.executed == 1
        workflow.activity("activity").status == ActivityStatus.ACTIVE

        when:
        systemActorScheduler.run(LocalDateTime.now().plusMinutes(1))
        workflow = getWorkflow()

        then:
        MockExceptionActivityHandler.executed == 2
        workflow.status == WorkflowStatus.TERMINATED
        workflow.activity("activity").status == ActivityStatus.FAILED

        when:
        systemActorScheduler.run(LocalDateTime.now().plusMinutes(10))

        then: // not executed anymore
        MockExceptionActivityHandler.executed == 2
    }

    def "Retry and fail activity on failure result"() {
        given:
        startWorkflow(builder()
            .systemActivity("activity")
            .resolutions("ok")
            .handler(MockFailActivityHandler.class)
            .maxAttempts(2)
            .attemptTimeoutInSeconds(5)
            .terminateWorkflowOnFailure(true)
            .completeWorkflowOnResolutions("ok")
            .add())

        when:
        systemActorScheduler.run(LocalDateTime.now())
        workflow = getWorkflow()

        then:
        MockFailActivityHandler.executed == 1
        workflow.activity("activity").status == ActivityStatus.ACTIVE

        when:
        systemActorScheduler.run(LocalDateTime.now().plusMinutes(1))
        workflow = getWorkflow()

        then:
        MockFailActivityHandler.executed == 2
        workflow.status == WorkflowStatus.TERMINATED
        workflow.activity("activity").status == ActivityStatus.FAILED

        when:
        systemActorScheduler.run(LocalDateTime.now().plusMinutes(10))

        then: // not executed anymore
        MockFailActivityHandler.executed == 2
    }

    def "Activity failure listeners"() {
        given:
        startWorkflow(builder()
            .systemActivity("activity")
            .resolutions("ok")
            .handler(MockFailActivityHandler.class)
            .maxAttempts(1)
            .attemptTimeoutInSeconds(5)
            .onFailure(MockActivityListener.class)
            .terminateWorkflowOnFailure(true)
            .completeWorkflowOnResolutions("ok")
            .add())

        when:
        systemActorScheduler.run(LocalDateTime.now())
        workflow = getWorkflow()

        then:
        workflow.activity("activity").status == ActivityStatus.FAILED
        MockActivityListener.executed == 1
    }


    def "Reenter activity"() {
        given:
        workflowWithReentry()

        when:
        workflowService.completeActivity(workflow.activity("rules").id, "ok", "")
        workflow = getWorkflow()

        then:
        workflow.activity("rules").status == ActivityStatus.COMPLETED
        workflow.activity("document check").status == ActivityStatus.ACTIVE
        workflow.activity("document call").status == ActivityStatus.WAITING
        workflow.activity("issue loan").status == ActivityStatus.WAITING

        when:
        workflowService.completeActivity(workflow.activity("document check").id, "not ok", "")
        workflow = getWorkflow()

        then:
        workflow.activity("document check").status == ActivityStatus.COMPLETED
        workflow.activity("document call").status == ActivityStatus.ACTIVE
        workflow.activity("issue loan").status == ActivityStatus.WAITING

        when:
        workflowService.completeActivity(workflow.activity("document call").id, "ok", "")
        workflow = getWorkflow()

        then:
        workflow.activity("document check").status == ActivityStatus.ACTIVE
        workflow.activity("document call").status == ActivityStatus.COMPLETED
        workflow.activity("issue loan").status == ActivityStatus.WAITING

        when:
        workflowService.completeActivity(workflow.activity("document check").id, "not ok", "")
        workflow = getWorkflow()

        then:
        workflow.activity("document check").status == ActivityStatus.COMPLETED
        workflow.activity("document call").status == ActivityStatus.ACTIVE
        workflow.activity("issue loan").status == ActivityStatus.WAITING

        when:
        workflowService.completeActivity(workflow.activity("document call").id, "ok", "")
        workflowService.completeActivity(workflow.activity("document check").id, "ok", "")
        workflow = getWorkflow()


        then:
        workflow.activity("document check").status == ActivityStatus.COMPLETED
        workflow.activity("document call").status == ActivityStatus.COMPLETED
        workflow.activity("issue loan").status == ActivityStatus.ACTIVE
        workflow.status == WorkflowStatus.ACTIVE

        and:
        workflow.activity("document check").attempts == 3L
        MockActivityListener.executed == 3

        when:
        workflowService.completeActivity(workflow.activity("issue loan").id, "ok", "")
        workflow = getWorkflow()

        then:
        workflow.activity("document check").status == ActivityStatus.COMPLETED
        workflow.activity("document call").status == ActivityStatus.COMPLETED
        workflow.activity("issue loan").status == ActivityStatus.COMPLETED
        workflow.status == WorkflowStatus.COMPLETED
    }

    // document check activity can be reentered after document call
    private Workflow workflowWithReentry() {
        startWorkflow(builder()
            .agentActivity("rules").resolutions("ok").add()

            .agentActivity("document check")
            .resolutions("ok", "not ok")
            .onStarted(MockActivityListener.class)
            .reactivationSupported(true)
            .waitForAny(completed("rules", "ok"), completed("document call", "ok"))
            .add()

            .agentActivity("document call")
            .resolutions("ok")
            .reactivationSupported(true)
            .waitForAll(completed("document check", "not ok"))
            .add()

            .agentActivity("issue loan")
            .resolutions("ok")
            .waitForAll(completed("document check", "ok"))
            .completeWorkflowOnResolutions("ok")
            .add())
    }

    def "Activity on close listeners are executed"() {
        given:
        startWorkflow(builder()
            .agentActivity("A")
            .resolutions("ok", "not ok")
            .onClosed(MockActivityListener.class)
            .completeWorkflowOnResolutions("ok")
            .add()
            .agentActivity("B")
            .resolutions("o")
            .onClosed(MockActivityListener.class)
            .add())

        when:
        workflowService.completeActivity(workflow.activity("A").id, "ok", "")
        workflow = getWorkflow()

        then:
        workflow.activity("A").status == ActivityStatus.COMPLETED
        workflow.activity("B").status == ActivityStatus.CANCELLED

        and: "on close listener executed for both activities"
        MockActivityListener.executed == 2
    }

    def "Workflow on terminated listeners are executed"() {
        given:
        startWorkflow(builder()
            .addOnTerminatedListener(MockWorkflowListener.class)
            .agentActivity("activity")
            .resolutions("ok")
            .add())

        when:
        workflowService.terminateWorkflow(workflow.id, "testing")

        then:
        MockWorkflowListener.executed == 1
        getWorkflow().status == WorkflowStatus.TERMINATED
        getWorkflow().terminateReason == "testing"
    }

    def "Start activity by trigger event"() {
        given:
        startWorkflow(builder()
            .systemActivity("A")
            .handler(MockActivityHandler.class)
            .activateOnTrigger(simpleTrigger)
            .resolutions("ok")
            .add())

        when:
        workflowService.trigger(workflowId, new Object())
        workflow = getWorkflow()

        then: "Not triggered by unknown event"
        workflow.activity("A").status == ActivityStatus.WAITING

        when:
        workflowService.trigger(workflowId, new SimpleEvent(value: 0))
        workflow = getWorkflow()

        then: "Not yet trigger as value doesn't match condition"
        workflow.activity("A").status == ActivityStatus.WAITING

        when:
        workflowService.trigger(workflowId, new SimpleEvent(value: 1))
        workflow = getWorkflow()

        then: "Activity started"
        workflow.activity("A").status == ActivityStatus.ACTIVE

        when:
        workflowService.trigger(workflowId, new SimpleEvent(value: 1))
        workflow = getWorkflow()

        then: "Activity was already started, nothing happens"
        workflow.activity("A").status == ActivityStatus.ACTIVE
    }

    def "Complete activity by trigger event"() {
        given:
        startWorkflow(builder()
            .agentActivity("A")
            .resolutions("ok")
            .completeOnTrigger(simpleTrigger, "ok")
            .completeWorkflowOnResolutions("ok")
            .add())

        when:
        workflowService.trigger(workflowId, new Object())
        workflow = getWorkflow()

        then: "Not triggered by unknown event"
        workflow.activity("A").status == ActivityStatus.ACTIVE

        when:
        workflowService.trigger(workflowId, new SimpleEvent(value: 1))
        workflow = getWorkflow()

        then:
        workflow.activity("A").status == ActivityStatus.COMPLETED

        and:
        workflow.status == WorkflowStatus.COMPLETED
    }

    def "Fail client activity by trigger event"() {
        given:
        startWorkflow(builder()
            .clientActivity("A")
            .resolutions("ok")
            .failOnTrigger(simpleTrigger, "error")
            .completeWorkflowOnResolutions("ok")
            .maxAttempts(2)
            .add())

        when:
        workflowService.trigger(workflowId, new Object())
        workflow = getWorkflow()

        then: "Not triggered by unknown event"
        workflow.activity("A").status == ActivityStatus.ACTIVE

        when:
        workflowService.trigger(workflowId, new SimpleEvent(value: 1))
        workflow = getWorkflow()

        then:
        with(workflow.activity("A")) {
            status == ActivityStatus.ACTIVE
            attempts == 1
        }
        workflow.status == WorkflowStatus.ACTIVE

        when:
        workflowService.trigger(workflowId, new SimpleEvent(value: 1))
        workflow = getWorkflow()

        then:
        workflow.activity("A").status == ActivityStatus.FAILED

        and:
        workflow.status == WorkflowStatus.TERMINATED
    }

    def "Workflow optimistic lock version is not updated on attribute changes"() {
        when:
        startWorkflow(builder()
            .clientActivity("activity")
            .resolutions("ok")
            .completeWorkflowOnResolutions("ok")
            .add())

        then:
        workflowRepository.getRequired(workflowId).entityVersion == 0

        when:
        workflowService.setAttribute(workflowId, "test", "test")

        then:
        workflowRepository.getRequired(workflowId).entityVersion == 0
    }

    static class SimpleEvent {
        long value
    }

    def simpleTrigger = new ActivityTrigger(SimpleEvent.class) {
        @Override
        Boolean apply(Object input) {
            return ((SimpleEvent) input).value > 0
        }
    }
}
