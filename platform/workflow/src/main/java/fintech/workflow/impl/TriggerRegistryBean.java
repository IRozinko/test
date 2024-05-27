package fintech.workflow.impl;

import com.google.common.collect.Maps;
import fintech.workflow.TriggerRegistry;
import fintech.workflow.spi.TriggerHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
class TriggerRegistryBean implements TriggerRegistry {

    private static final Map<String, Class<? extends TriggerHandler>> HANDLERS = Maps.newHashMap();

    @Override
    public void addTriggerHandler(String name, Class<? extends TriggerHandler> clazz) {
        HANDLERS.put(name, clazz);
    }

    @Override
    public Class<? extends TriggerHandler> getTriggerHandler(String name) {
        return HANDLERS.get(name);
    }
}
