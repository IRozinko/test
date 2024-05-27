package fintech.workflow.spi;

import fintech.workflow.ActivityStatus;
import fintech.workflow.Actor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface ActivityDefinition {

    String getActivityName();

    Actor getActor();

    Set<String> getResolutions();

    boolean isTerminateWorkflowOnFailure();

    boolean isReactivationSupported();

    int getMaxAttempts();

    int getAttemptTimeoutInSeconds();

    int getInitialDelayInSeconds();

    Set<String> getTerminateWorkflowOnResolutions();

    Set<String> getCompleteWorkflowOnResolutions();

    Set<String> getExpireWorkflowOnResolutions();

    List<BeanMetadata<ActivityListener>> getOnStartedListeners();

    List<Pair<String, BeanMetadata<ActivityListener>>> getOnCompletedListeners();

    List<BeanMetadata<ActivityListener>> getOnClosedListeners();

    List<BeanMetadata<ActivityListener>> getOnFailedListeners();

    Set<Pair<String, Set<String>>> getWaitForAll();

    Set<Pair<String, Set<String>>> getWaitForAny();

    Optional<BeanMetadata<ActivityHandler>> getHandler();

    List<DynamicListenerMetadata> getDynamicListeners();

    List<Pair<String, Predicate<ActivityContext>>> getAutoCompletePredicatePreconditions();

    ActivityStatus getInitialStatus();

    WorkflowDefinition getWorkflow();

    Optional<ActivityTrigger> getActivateOnTrigger(Class<?> eventClass);

    Optional<Pair<ResolutionWithDetail, ActivityTrigger>> getCompleteOnTrigger(Class<?> eventClass);

    Optional<Pair<String, ActivityTrigger>> getFailOnTrigger(Class<?> eventClass);

    List<ActivityTrigger> getResetDelayOnTriggers(Class<?> eventClass);

    List<Pair<String, BeanMetadata<AutoCompletePrecondition>>> getAutoCompletePreconditions();

    int getExpiresInSeconds();

    String getExpiresWithResolution();

    boolean isExpiresDuringNonBusinessDays();

    String getUiState();

}
