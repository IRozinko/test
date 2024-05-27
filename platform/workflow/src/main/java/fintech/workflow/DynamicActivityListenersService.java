package fintech.workflow;

public interface DynamicActivityListenersService {

    void addOrEditListener(UpdateDynamicActivityListenerCommand command);

    void removeDynamicListener(Long id);

    void runOnStartedListenerIfPresent(Workflow workflow, Activity activity);

    void runOnCompletedListenerIfPresent(Workflow workflow, Activity activity, String resolution);

}
