package fintech.dc.impl;

import fintech.Validate;
import fintech.dc.spi.ActionHandler;
import fintech.dc.spi.BulkActionHandler;
import fintech.dc.spi.ConditionHandler;
import fintech.dc.spi.DcRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DcRegistryBean implements DcRegistry {

    private final Map<String, Class<? extends ActionHandler>> actionHandlers = new HashMap<>();
    private final Map<String, Class<? extends ConditionHandler>> conditionHandlers = new HashMap<>();
    private final Map<String, Class<? extends BulkActionHandler>> bulkActionHandlers = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void registerActionHandler(String type, Class<? extends ActionHandler> handlerClass) {
        actionHandlers.put(type, handlerClass);
    }

    @Override
    public void registerConditionHandler(String type, Class<? extends ConditionHandler> handlerClass) {
        conditionHandlers.put(type, handlerClass);
    }

    @Override
    public void registerBulkActionHandler(String type, Class<? extends BulkActionHandler> handlerClass) {
        bulkActionHandlers.put(type, handlerClass);
    }

    @Override
    public ConditionHandler getConditionHandler(String type) {
        Class<? extends ConditionHandler> handlerClass = conditionHandlers.get(type);
        Validate.notNull(handlerClass, "Condition handler not found by type: [%s]", type);
        return applicationContext.getBean(handlerClass);
    }

    @Override
    public ActionHandler getActionHandler(String type) {
        Class<? extends ActionHandler> handlerClass = actionHandlers.get(type);
        Validate.notNull(handlerClass, "Action handler not found by type: [%s]", type);
        return applicationContext.getBean(handlerClass);
    }

    @Override
    public BulkActionHandler getBulkActionHandler(String type) {
        Class<? extends BulkActionHandler> handlerClass = bulkActionHandlers.get(type);
        Validate.notNull(handlerClass, "Bulk action handler not found by type: [%s]", type);
        return applicationContext.getBean(handlerClass);
    }
}
