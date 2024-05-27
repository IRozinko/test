package fintech.workflow;

public interface TriggerService {

    Long addTrigger(AddTriggerCommand command);

    void executeTrigger(Long triggerId);

    void failTrigger(Long triggerId, String error);
}
