package fintech.workflow

import fintech.workflow.spi.ActivityContext
import fintech.workflow.spi.ActivityListener
import fintech.workflow.spi.AutoCompletePrecondition
import fintech.workflow.spi.WorkflowBuilder
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

class WorkflowPreconditionsTest extends WorkflowSpecification {

    @Component
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    static class True implements AutoCompletePrecondition {
        @Override
        boolean isTrueFor(ActivityContext context) {
            return true
        }
    }

    @Component
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    static class False implements AutoCompletePrecondition {
        @Override
        boolean isTrueFor(ActivityContext context) {
            return false
        }
    }

    @Component
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    static class CountInvocations implements ActivityListener {
        static int invocations

        @Override
        void handle(ActivityContext context) {
            invocations++
        }
    }

    static final String withPrecondition = 'withPrecondition'
    static final String withoutPrecondition = 'withoutPrecondition'
    static final String Good = 'Good!'

    def setup() {
        CountInvocations.invocations = 0
    }

    def "Activity without a precondition should be active"() {
        given:
            activityWithoutPrecondition()
        expect:
            activity(withoutPrecondition).status == ActivityStatus.ACTIVE
            CountInvocations.invocations == 1
    }

    def "Activity with unsatisfied precondition should remain active"() {
        given:
            activityWithPrecondition(False)
        expect:
            activity(withPrecondition).status == ActivityStatus.ACTIVE
            CountInvocations.invocations == 1
    }

    def "Activity with satisfied precondition should be completed without calling on started listeners"() {
        given:
            activityWithPrecondition(True)
        expect:
            with(activity(withPrecondition)) {
                status == ActivityStatus.COMPLETED
                resolution == Good
            }
            CountInvocations.invocations == 0
    }

    private activityWithoutPrecondition() {
        startWorkflow(buildActivity(withoutPrecondition).add())
    }

    private activityWithPrecondition(Class<? extends AutoCompletePrecondition> predicate) {
        startWorkflow(
            buildActivity(withPrecondition)
                .autoCompleteWithResolutionIf(Good, predicate)
                .add()
        )
    }

    private WorkflowBuilder.ActivityBuilder buildActivity(String name) {
        builder()
            .clientActivity(name)
            .resolutions(Good)
            .completeWorkflowOnResolutions(Good)
            .onStarted(CountInvocations)
    }

    private activity(String name) {
        getWorkflow().activity(name)
    }
}
