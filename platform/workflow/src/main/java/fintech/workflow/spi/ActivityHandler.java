package fintech.workflow.spi;


import fintech.workflow.WorkflowService;

public interface ActivityHandler {

    /**
     * fixme: just for avoid copy-pasting
     * @see WorkflowService::failActivity
     */
    default boolean checkMaxAttemptsExceeded(ActivityContext context) {
        long attempts = context.getActivity().getAttempts() +1;
        int maxAttempts = context.getActivityDefinition().getMaxAttempts();

        return maxAttempts != 0 && attempts >= maxAttempts;
    }

    /**
     * @return activity resolution
     */
    ActivityResult handle(ActivityContext context);
}
