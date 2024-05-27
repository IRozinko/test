package fintech.activity.impl;

import fintech.Validate;
import fintech.activity.spi.ActivityRegistry;
import fintech.activity.spi.BulkActionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ActivityRegistryBean implements ActivityRegistry {

    private final Map<String, Class<? extends BulkActionHandler>> bulkActionHandlers = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void registerBulkActionHandler(String type, Class<? extends BulkActionHandler> handlerClass) {
        bulkActionHandlers.put(type, handlerClass);
    }

    @Override
    public BulkActionHandler getBulkActionHandler(String type) {
        Class<? extends BulkActionHandler> handlerClass = bulkActionHandlers.get(type);
        Validate.notNull(handlerClass, "Bulk action handler not found by type: [%s]", type);
        return applicationContext.getBean(handlerClass);
    }
}
