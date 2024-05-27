package fintech.workflow.spi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import fintech.Validate;
import fintech.workflow.ActivityListenerStatus;
import fintech.workflow.ActivityStatus;
import fintech.workflow.Actor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;

class ActivityDefinitionImpl implements ActivityDefinition {

    private final WorkflowDefinition workflowDefinition;
    private final String activityName;
    private final Actor actor;
    private final Set<String> resolutions = new HashSet<>();
    private final List<BeanMetadata<ActivityListener>> onStartedListeners = new ArrayList<>();
    private final List<DynamicListenerMetadata> dynamicListeners = new ArrayList<>();
    private final List<Pair<String, BeanMetadata<ActivityListener>>> onCompletedListeners = new ArrayList<>();
    private final List<BeanMetadata<ActivityListener>> onClosedListeners = new ArrayList<>();
    private final List<BeanMetadata<ActivityListener>> onFailedListeners = new ArrayList<>();
    private final Set<String> terminateWorkflowOnResolutions = new HashSet<>();
    private final Set<String> completeWorkflowOnResolutions = new HashSet<>();
    private final Set<String> expireWorkflowOnResolutions = new HashSet<>();
    private final Set<Pair<String, Set<String>>> waitForAll = new HashSet<>();
    private final Set<Pair<String, Set<String>>> waitForAny = new HashSet<>();
    private final Map<Class, ActivityTrigger> activateOnTriggers = new HashMap<>();
    private final Map<Class, Pair<ResolutionWithDetail, ActivityTrigger>> completeOnTriggers = new HashMap<>();
    private final Map<Class, Pair<String, ActivityTrigger>> failOnTriggers = new HashMap<>();
    private final Map<Class, List<ActivityTrigger>> resetDelayOnTriggers = new HashMap<>();
    private BeanMetadata<ActivityHandler> handlerClass;
    private int maxAttempts = 0;
    private int initialDelayInSeconds = 0;
    private int attemptTimeoutInSeconds = 0;
    private boolean terminateWorkflowOnFailure = true;
    private ActivityStatus initialStatus = ActivityStatus.ACTIVE;
    private boolean reactivationSupported;
    private List<Pair<String, BeanMetadata<AutoCompletePrecondition>>> autoCompletePreconditions = new ArrayList<>();
    private List<Pair<String, Predicate<ActivityContext>>> autoCompletePredicatePreconditions = new ArrayList<>();
    private int expiresInSeconds = -1;
    private String expiresWithResolution;
    private boolean expiresDuringNonBusinessDays = true;
    private String uiState;

    ActivityDefinitionImpl(WorkflowDefinition workflowDefinition, String activityName, Actor actor) {
        this.workflowDefinition = workflowDefinition;
        this.activityName = Validate.notBlank(activityName);
        this.actor = Validate.notNull(actor);
    }

    @Override
    public String getActivityName() {
        return activityName;
    }

    @Override
    public Actor getActor() {
        return actor;
    }

    @Override
    public Set<String> getResolutions() {
        return ImmutableSet.copyOf(resolutions);
    }

    @Override
    public boolean isTerminateWorkflowOnFailure() {
        return terminateWorkflowOnFailure;
    }

    @Override
    public int getMaxAttempts() {
        return maxAttempts;
    }

    @Override
    public int getAttemptTimeoutInSeconds() {
        return attemptTimeoutInSeconds;
    }

    @Override
    public int getInitialDelayInSeconds() {
        return this.initialDelayInSeconds;
    }

    @Override
    public Set<String> getTerminateWorkflowOnResolutions() {
        return terminateWorkflowOnResolutions;
    }

    @Override
    public Set<String> getCompleteWorkflowOnResolutions() {
        return ImmutableSet.copyOf(this.completeWorkflowOnResolutions);
    }

    @Override
    public Set<String> getExpireWorkflowOnResolutions() {
        return ImmutableSet.copyOf(this.expireWorkflowOnResolutions);
    }

    @Override
    public List<BeanMetadata<ActivityListener>> getOnStartedListeners() {
        return ImmutableList.copyOf(onStartedListeners);
    }

    @Override
    public List<Pair<String, BeanMetadata<ActivityListener>>> getOnCompletedListeners() {
        return ImmutableList.copyOf(onCompletedListeners);
    }

    @Override
    public List<BeanMetadata<ActivityListener>> getOnClosedListeners() {
        return ImmutableList.copyOf(onClosedListeners);
    }

    @Override
    public List<BeanMetadata<ActivityListener>> getOnFailedListeners() {
        return ImmutableList.copyOf(onFailedListeners);
    }

    @Override
    public Set<Pair<String, Set<String>>> getWaitForAll() {
        return ImmutableSet.copyOf(waitForAll);
    }

    @Override
    public Set<Pair<String, Set<String>>> getWaitForAny() {
        return ImmutableSet.copyOf(waitForAny);
    }

    @Override
    public Optional<BeanMetadata<ActivityHandler>> getHandler() {
        return Optional.ofNullable(handlerClass);
    }

    @Override
    public List<DynamicListenerMetadata> getDynamicListeners() {
        return ImmutableList.copyOf(dynamicListeners);
    }

    @Override
    public List<Pair<String, BeanMetadata<AutoCompletePrecondition>>> getAutoCompletePreconditions() {
        return ImmutableList.copyOf(this.autoCompletePreconditions);
    }

    @Override
    public List<Pair<String, Predicate<ActivityContext>>> getAutoCompletePredicatePreconditions() {
        return ImmutableList.copyOf(this.autoCompletePredicatePreconditions);
    }

    void addResolutions(String... resolutions) {
        Collections.addAll(this.resolutions, resolutions);
    }

    void addOnStartedListener(Class<? extends ActivityListener> listener, Object... args) {
        this.onStartedListeners.add(new BeanMetadata<>(listener, args));
    }

    void addDynamicListenerOnStarted(String triggerName, String... args) {
        this.dynamicListeners.add(new DynamicListenerMetadata(ActivityListenerStatus.STARTED, triggerName, null, args, Duration.ZERO, false));
    }

    void addDynamicListenerOnStarted(String triggerName, Duration delay, boolean fromMidnight, String... args) {
        Validate.isTrue(delay != null, "Delay is null");
        this.dynamicListeners.add(new DynamicListenerMetadata(ActivityListenerStatus.STARTED, triggerName, null, args, delay, fromMidnight));
    }

    void addDynamicListenerOnCompleted(String resolution, String triggerName, String... args) {
        Validate.isTrue(this.resolutions.contains(resolution), "Unknown resolution %s", resolution);
        this.dynamicListeners.add(new DynamicListenerMetadata(ActivityListenerStatus.COMPLETED, triggerName, resolution, args, null, null));
    }

    void addOnClosedListener(Class<? extends ActivityListener> listener, Object... args) {
        this.onClosedListeners.add(new BeanMetadata<>(listener, args));
    }

    void addOnFailedListener(Class<? extends ActivityListener> listener, Object... args) {
        this.onFailedListeners.add(new BeanMetadata<>(listener, args));
    }

    void addOnCompletedListener(String resolution, Class<? extends ActivityListener> listenerClass, Object... args) {
        Validate.isTrue(this.resolutions.contains(resolution), "Unknown resolution %s", resolution);
        this.onCompletedListeners.add(ImmutablePair.of(resolution, new BeanMetadata<>(listenerClass, args)));
    }

    void setMaxAttempts(int maxAttempts) {
        Validate.isTrue(maxAttempts >= 0, "Max attempts must be >= 0");
        this.maxAttempts = maxAttempts;
    }

    void setAttemptTimeoutInSeconds(int timeout) {
        Validate.isTrue(timeout >= 0, "Timeout must be >= 0");
        this.attemptTimeoutInSeconds = timeout;
    }

    void setInitialDelayInSeconds(int initialDelayInSeconds) {
        Validate.isTrue(initialDelayInSeconds >= 0, "Delay must be >= 0");
        this.initialDelayInSeconds = initialDelayInSeconds;
    }

    void setTerminateWorkflowOnFailure(boolean terminate) {
        this.terminateWorkflowOnFailure = terminate;
    }

    void addTerminateWorkflowOnResolutions(String... resolutions) {
        assertValidResolutions(resolutions);
        Collections.addAll(this.terminateWorkflowOnResolutions, resolutions);
    }

    void addCompleteWorkflowOnResolutions(String... resolutions) {
        assertValidResolutions(resolutions);
        Collections.addAll(this.completeWorkflowOnResolutions, resolutions);
    }

    void addExpireWorkflowOnResolutions(String... resolutions) {
        assertValidResolutions(resolutions);
        Collections.addAll(this.expireWorkflowOnResolutions, resolutions);
    }

    void addActivateOnTrigger(ActivityTrigger trigger) {
        Validate.isTrue(this.waitForAny.isEmpty(), "Can not have both activate triggers and wait for any configs");
        Validate.isTrue(this.waitForAll.isEmpty(), "Can not have both activate triggers and wait for all configs");
        ActivityTrigger previous = this.activateOnTriggers.putIfAbsent(trigger.getEventClass(), trigger);
        Validate.isTrue(previous == null, "Activate on trigger already registered for event class [%s]", trigger.getEventClass());
        initialStatus = ActivityStatus.WAITING;
    }

    void addCompleteOnTrigger(ActivityTrigger trigger, String resolution, String resolutionDetail) {
        Validate.isTrue(this.resolutions.contains(resolution), "Unknown resolution %s", resolution);
        Pair<ResolutionWithDetail, ActivityTrigger> previous = this.completeOnTriggers.putIfAbsent(trigger.getEventClass(), ImmutablePair.of(new ResolutionWithDetail(resolution, resolutionDetail), trigger));
        Validate.isTrue(previous == null, "Complete on trigger already registered for event class [%s]", trigger.getEventClass());
    }

    void addFailOnTrigger(ActivityTrigger trigger, String error) {
        Pair<String, ActivityTrigger> previous = this.failOnTriggers.putIfAbsent(trigger.getEventClass(), ImmutablePair.of(error, trigger));
        Validate.isTrue(previous == null, "Fail on trigger already registered for event class [%s]", trigger.getEventClass());
    }

    void setReactivationSupported(boolean reactivationSupported) {
        this.reactivationSupported = reactivationSupported;
    }

    private void assertValidResolutions(String... resolutions) {
        for (String resolution : resolutions) {
            Validate.isTrue(this.resolutions.contains(resolution), "Unknown resolution %s", resolution);
        }
    }

    @SafeVarargs
    final void addWaitForAll(Pair<String, Set<String>>... activityAndResolution) {
        Validate.isTrue(this.activateOnTriggers.isEmpty(), "Can not have both activate triggers and wait for all configs");
        Validate.notEmpty(activityAndResolution);
        Validate.isTrue(this.waitForAny.isEmpty(), "Can't have both wait for all and wait for any");
        assertNotWaitingSelf(activityAndResolution);
        Collections.addAll(this.waitForAll, activityAndResolution);
        initialStatus = ActivityStatus.WAITING;
    }

    @SafeVarargs
    final void addWaitForAny(Pair<String, Set<String>>... activityAndResolution) {
        Validate.isTrue(this.activateOnTriggers.isEmpty(), "Can not have both activate triggers and wait for all configs");
        Validate.notEmpty(activityAndResolution);
        Validate.isTrue(this.waitForAll.isEmpty(), "Can't have both wait for all and wait for any");
        assertNotWaitingSelf(activityAndResolution);
        Collections.addAll(this.waitForAny, activityAndResolution);
        initialStatus = ActivityStatus.WAITING;
    }

    private void assertNotWaitingSelf(Pair<String, Set<String>>[] activityAndResolution) {
        for (Pair<String, Set<String>> p : activityAndResolution) {
            Validate.isTrue(!p.getLeft().equals(this.activityName), "Can't wait on self: [%s]", this.activityName);
        }
    }

    void setHandler(Class<? extends ActivityHandler> handlerClass, Object... args) {
        Validate.isTrue(this.actor == Actor.SYSTEM, "Only system actor activities may have handler");
        this.handlerClass = new BeanMetadata<>(handlerClass, args);
    }

    @Override
    public ActivityStatus getInitialStatus() {
        return this.initialStatus;
    }

    @Override
    public WorkflowDefinition getWorkflow() {
        return this.workflowDefinition;
    }

    @Override
    public Optional<ActivityTrigger> getActivateOnTrigger(Class<?> eventClass) {
        ActivityTrigger trigger = this.activateOnTriggers.get(eventClass);
        return Optional.ofNullable(trigger);
    }

    @Override
    public Optional<Pair<ResolutionWithDetail, ActivityTrigger>> getCompleteOnTrigger(Class<?> eventClass) {
        Pair<ResolutionWithDetail, ActivityTrigger> trigger = this.completeOnTriggers.get(eventClass);
        return Optional.ofNullable(trigger);
    }

    @Override
    public Optional<Pair<String, ActivityTrigger>> getFailOnTrigger(Class<?> eventClass) {
        Pair<String, ActivityTrigger> trigger = this.failOnTriggers.get(eventClass);
        return Optional.ofNullable(trigger);
    }

    @Override
    public List<ActivityTrigger> getResetDelayOnTriggers(Class<?> eventClass) {
        return this.resetDelayOnTriggers.getOrDefault(eventClass, emptyList());
    }

    @Override
    public boolean isReactivationSupported() {
        return this.reactivationSupported;
    }


    void addResetDelayOnTrigger(ActivityTrigger trigger) {
        List<ActivityTrigger> updatedTriggers = this
            .resetDelayOnTriggers
            .getOrDefault(trigger.getEventClass(), new ArrayList<>());
        updatedTriggers.add(trigger);
        this.resetDelayOnTriggers.put(trigger.getEventClass(), updatedTriggers);
    }

    void addAutoCompletePrecondition(String resolution, Class<? extends AutoCompletePrecondition> conditionClass, Object... args) {
        Validate.isTrue(this.resolutions.contains(resolution), "Unknown resolution %s", resolution);
        this.autoCompletePreconditions.add(ImmutablePair.of(resolution, new BeanMetadata<>(conditionClass, args)));
    }

    void addAutoCompletePrecondition(String resolution, Predicate<ActivityContext> predicate) {
        Validate.isTrue(this.resolutions.contains(resolution), "Unknown resolution %s", resolution);
        this.autoCompletePredicatePreconditions.add(ImmutablePair.of(resolution, predicate));
    }

    @Override
    public int getExpiresInSeconds() {
        return expiresInSeconds;
    }

    void setExpiresInSeconds(int expiresInSeconds) {
        Validate.isTrue(expiresInSeconds >= 0, "Invalid expires in seconds value");
        this.expiresInSeconds = expiresInSeconds;
    }

    @Override
    public String getExpiresWithResolution() {
        return expiresWithResolution;
    }

    void setExpiresWithResolution(String expiresWithResolution) {
        assertValidResolutions(expiresWithResolution);
        this.expiresWithResolution = expiresWithResolution;
    }

    @Override
    public boolean isExpiresDuringNonBusinessDays() {
        return expiresDuringNonBusinessDays;
    }

    void setExpiresDuringNonBusinessDays(boolean expiresDuringNonBusinessDays) {
        this.expiresDuringNonBusinessDays = expiresDuringNonBusinessDays;
    }

    public String getUiState() {
        return uiState;
    }

    public void setUiState(String uiState) {
        this.uiState = uiState;
    }
}
