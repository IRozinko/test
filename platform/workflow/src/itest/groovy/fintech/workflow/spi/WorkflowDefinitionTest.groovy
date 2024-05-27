package fintech.workflow.spi

import fintech.workflow.Actor
import spock.lang.Specification

class WorkflowDefinitionTest extends Specification {

    def "GetActivity"() {
        given:
        WorkflowDefinition wf = testWorkflowDefinition()


        when:
        def foundActivity = wf.getActivity("Second")

        then:
        foundActivity.isPresent()
        foundActivity.get().getActivityName() == "Second"

        when:
        foundActivity = wf.getActivity("NotExisted")

        then:
        !foundActivity.isPresent()
    }

    def "GetActivities"() {
        given:
        WorkflowDefinition wf = testWorkflowDefinition()

        when:
        def activities = wf.getActivities("Second", "Fifth")

        then:
        activities.size() == 3
        activities.collect { it -> it.getActivityName() } == ["Second", "Third", "Fourth"]

        when:
        wf.getActivities("Fifth", "Second")

        then:
        thrown IllegalArgumentException

        when:
        wf.getActivities(null, "Second")

        then:
        thrown IllegalArgumentException

        when:
        wf.getActivities("First", null)

        then:
        thrown IllegalArgumentException
    }

    def testWorkflowDefinition() {
        WorkflowDefinition wf = new WorkflowDefinitionImpl("WF")
        wf.addActivity(new ActivityDefinitionImpl(wf, "First", Actor.SYSTEM))
        wf.addActivity(new ActivityDefinitionImpl(wf, "Second", Actor.SYSTEM))
        wf.addActivity(new ActivityDefinitionImpl(wf, "Third", Actor.SYSTEM))
        wf.addActivity(new ActivityDefinitionImpl(wf, "Fourth", Actor.SYSTEM))
        wf.addActivity(new ActivityDefinitionImpl(wf, "Fifth", Actor.SYSTEM))
        return wf
    }
}
