package fintech.workflow;

import fintech.workflow.spi.TriggerHandler;

public interface TriggerRegistry {

    void addTriggerHandler(String name, Class<? extends TriggerHandler> clazz);

    Class<? extends TriggerHandler> getTriggerHandler(String name);
}
