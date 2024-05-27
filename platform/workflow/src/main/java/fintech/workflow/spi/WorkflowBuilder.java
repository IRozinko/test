package fintech.workflow.spi;

import com.google.common.collect.ImmutableSet;
import fintech.Validate;
import fintech.workflow.Actor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public class WorkflowBuilder {

    private WorkflowDefinitionImpl workflow;
    private ActivityBuilder currentActivity;

    public WorkflowBuilder(String workflow) {
        this.workflow = new WorkflowDefinitionImpl(workflow);
    }

    public WorkflowBuilder(String workflow, Integer workflowVersion) {
        this.workflow = new WorkflowDefinitionImpl(workflow, workflowVersion);
    }

    public ActivityBuilder systemActivity(String name) {
        this.currentActivity = new ActivityBuilder(name, Actor.SYSTEM, this);
        this.currentActivity.maxAttempts(5);
        return this.currentActivity;
    }

    public ActivityBuilder agentActivity(String name) {
        this.currentActivity = new ActivityBuilder(name, Actor.AGENT, this);
        return this.currentActivity;
    }

    public ActivityBuilder clientActivity(String name) {
        this.currentActivity = new ActivityBuilder(name, Actor.CLIENT, this);
        return this.currentActivity;
    }

    public WorkflowBuilder addOnCompletedListener(Class<? extends WorkflowListener> listenerClass, Object... args) {
        assertClassIsSpringComponent(listenerClass);
        assertClassIsSpringPrototypeScope(listenerClass);
        workflow.addOnCompletedListener(listenerClass, args);
        return this;
    }

    public WorkflowBuilder addOnTerminatedListener(Class<? extends WorkflowListener> listenerClass, Object... args) {
        assertClassIsSpringComponent(listenerClass);
        assertClassIsSpringPrototypeScope(listenerClass);
        workflow.addOnTerminatedListener(listenerClass, args);
        return this;
    }

    public WorkflowBuilder addOnExpiredListener(Class<? extends WorkflowListener> listenerClass, Object... args) {
        assertClassIsSpringComponent(listenerClass);
        assertClassIsSpringPrototypeScope(listenerClass);
        workflow.addOnExpiredListener(listenerClass, args);
        return this;
    }

    public WorkflowDefinition build() {
        workflow.getActivities().forEach((activity) -> activity.getWaitForAll().forEach((a) -> Validate.isTrue(activityAndResolutionExists(a.getLeft(), a.getRight()), "Unknown wait for all activity & resolution: %s", a)));
        workflow.getActivities().forEach((activity) -> activity.getWaitForAny().forEach((a) -> Validate.isTrue(activityAndResolutionExists(a.getLeft(), a.getRight()), "Unknown wait for any activity & resolution: %s", a)));
        return this.workflow;
    }

    public class ActivityBuilder {
        private final ActivityDefinitionImpl activity;
        private final WorkflowBuilder workflowBuilder;

        ActivityBuilder(String activity, Actor actor, WorkflowBuilder workflowBuilder) {
            this.activity = new ActivityDefinitionImpl(workflow, activity, actor);
            this.workflowBuilder = checkNotNull(workflowBuilder);
        }


        public WorkflowBuilder add() {
            workflowBuilder.workflow.addActivity(activity);
            workflowBuilder.currentActivity = null;
            return workflowBuilder;
        }

        public ActivityBuilder resolutions(String... resolutions) {
            activity.addResolutions(resolutions);
            return this;
        }

        public ActivityBuilder onStarted(Class<? extends ActivityListener> listenerClass, Object... args) {
            assertClassIsSpringComponent(listenerClass);
            assertClassIsSpringPrototypeScope(listenerClass);
            activity.addOnStartedListener(listenerClass, args);
            return this;
        }

        public ActivityBuilder onStartedDynamic(String triggerName, String... args) {
            activity.addDynamicListenerOnStarted(triggerName, args);
            return this;
        }

        public ActivityBuilder onStartedDynamic(String triggerName, Duration delay, String... args) {
            return onStartedDynamic(triggerName, delay, false, args);
        }

        public ActivityBuilder onStartedDynamic(String triggerName, Duration delay, boolean fromMidnight, String... args) {
            activity.addDynamicListenerOnStarted(triggerName, delay, fromMidnight, args);
            return this;
        }

        public ActivityBuilder onResolutionDynamic(String resolution, String triggerName, String... args) {
            activity.addDynamicListenerOnCompleted(resolution, triggerName, args);
            return this;
        }

        public ActivityBuilder onClosed(Class<? extends ActivityListener> listenerClass, Object... args) {
            assertClassIsSpringComponent(listenerClass);
            assertClassIsSpringPrototypeScope(listenerClass);
            activity.addOnClosedListener(listenerClass, args);
            return this;
        }

        public ActivityBuilder onFailure(Class<? extends ActivityListener> listenerClass, Object... args) {
            assertClassIsSpringComponent(listenerClass);
            assertClassIsSpringPrototypeScope(listenerClass);
            activity.addOnFailedListener(listenerClass, args);
            return this;
        }

        public ActivityBuilder onResolution(String resolution, Class<? extends ActivityListener> listenerClass,
                                            Object... args) {
            assertClassIsSpringComponent(listenerClass);
            assertClassIsSpringPrototypeScope(listenerClass);
            activity.addOnCompletedListener(resolution, listenerClass, args);
            return this;
        }

        public ActivityBuilder maxAttempts(int maxAttempts) {
            activity.setMaxAttempts(maxAttempts);
            return this;
        }

        public ActivityBuilder attemptTimeoutInSeconds(int timeout) {
            activity.setAttemptTimeoutInSeconds(timeout);
            return this;
        }

        /**
         * the delay between the instant when this activity becomes {@link fintech.workflow.ActivityStatus#ACTIVE ACTIVE}
         * and the execution of the handler attached by {@link ActivityBuilder#handler(Class, Object...) handler()} method
         */
        public ActivityBuilder initialDelayInSeconds(int delay) {
            activity.setInitialDelayInSeconds(delay);
            return this;
        }

        public ActivityBuilder terminateWorkflowOnFailure(boolean terminate) {
            activity.setTerminateWorkflowOnFailure(terminate);
            return this;
        }

        public ActivityBuilder terminateWorkflowOnResolutions(String... resolutions) {
            activity.addTerminateWorkflowOnResolutions(resolutions);
            return this;
        }

        public ActivityBuilder completeWorkflowOnResolutions(String... resolutions) {
            activity.addCompleteWorkflowOnResolutions(resolutions);
            return this;
        }

        public ActivityBuilder expireWorkflowOnResolutions(String... resolutions) {
            activity.addExpireWorkflowOnResolutions(resolutions);
            return this;
        }

        public ActivityBuilder reactivationSupported(boolean supported) {
            activity.setReactivationSupported(supported);
            return this;
        }

        public ActivityBuilder activateOnTrigger(ActivityTrigger trigger) {
            activity.addActivateOnTrigger(trigger);
            return this;
        }

        public ActivityBuilder completeOnTrigger(ActivityTrigger trigger, String resolution) {
            activity.addCompleteOnTrigger(trigger, resolution, "");
            return this;
        }

        public ActivityBuilder completeOnTrigger(ActivityTrigger trigger, String resolution, String resolutionDetail) {
            activity.addCompleteOnTrigger(trigger, resolution, resolutionDetail);
            return this;
        }

        public ActivityBuilder failOnTrigger(ActivityTrigger trigger, String error) {
            activity.addFailOnTrigger(trigger, error);
            return this;
        }

        @SafeVarargs
        public final ActivityBuilder waitForAll(Pair<String, Set<String>>... activityAndResolutions) {
            activity.addWaitForAll(activityAndResolutions);
            return this;
        }

        @SafeVarargs
        public final ActivityBuilder waitForAny(Pair<String, Set<String>>... activityAndResolutions) {
            activity.addWaitForAny(activityAndResolutions);
            return this;
        }

        public ActivityBuilder handler(Class<? extends ActivityHandler> handlerClass, Object... args) {
            assertIsSpringPrototype(handlerClass);
            activity.setHandler(handlerClass, args);
            return this;
        }

        public ActivityBuilder resetDelayOnTrigger(ActivityTrigger trigger) {
            activity.addResetDelayOnTrigger(trigger);
            return this;
        }

        public ActivityBuilder autoCompleteWithResolutionIf(String resolution,
                                                            Class<? extends AutoCompletePrecondition> conditionClass,
                                                            Object... args) {
            assertClassIsSpringComponent(conditionClass);
            assertClassIsSpringPrototypeScope(conditionClass);
            activity.addAutoCompletePrecondition(resolution, conditionClass, args);
            return this;
        }

        public ActivityBuilder autoCompleteWithResolutionIf(String resolution, Predicate<ActivityContext> predicate) {
            activity.addAutoCompletePrecondition(resolution, predicate);
            return this;
        }

        public ActivityBuilder expires(int expiresInSeconds, String expiresWithResolution) {
            activity.setExpiresInSeconds(expiresInSeconds);
            activity.setExpiresWithResolution(expiresWithResolution);
            return this;
        }

        public ActivityBuilder expires(int expiresInSeconds, String expiresWithResolution,
                                       boolean expiresDuringWeekend) {
            activity.setExpiresInSeconds(expiresInSeconds);
            activity.setExpiresWithResolution(expiresWithResolution);
            activity.setExpiresDuringNonBusinessDays(expiresDuringWeekend);
            return this;
        }

        public ActivityBuilder withUiState(String state) {
            activity.setUiState(state);
            return this;
        }

    }

    private boolean activityAndResolutionExists(String activity, Set<String> resolution) {
        return this.workflow.getActivities().stream().anyMatch((a) ->
            activity.equals(a.getActivityName())
                && a.getResolutions().containsAll(resolution));
    }

    public static Pair<String, Set<String>> completed(String activity, String resolution) {
        return new ImmutablePair<>(activity, ImmutableSet.of(resolution));
    }

    public static Pair<String, Set<String>> completed(String activity, String... resolutions) {
        return new ImmutablePair<>(activity, new HashSet<>(Arrays.asList(resolutions)));
    }

    private static void assertIsSpringPrototype(Class<?> klass) {
        assertClassIsSpringComponent(klass);
        assertClassIsSpringPrototypeScope(klass);
    }

    private static void assertClassIsSpringComponent(Class<?> componentClass) {
        Validate.notNull(AnnotationUtils.findAnnotation(componentClass, Component.class), "Class %s is not annotated with @Component", componentClass);
    }

    private static void assertClassIsSpringPrototypeScope(Class<?> componentClass) {
        Scope scope = AnnotationUtils.findAnnotation(componentClass, Scope.class);
        Validate.isTrue(scope != null && BeanDefinition.SCOPE_PROTOTYPE.equals(scope.value()), "Class %s is not annotated with @Scope(prototype)", componentClass);
    }

}
