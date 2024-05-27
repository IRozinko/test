package fintech.dc.spi;

public interface DcRegistry {

    void registerActionHandler(String type, Class<? extends ActionHandler> handlerClass);

    void registerConditionHandler(String type, Class<? extends ConditionHandler> handlerClass);

    void registerBulkActionHandler(String type, Class<? extends BulkActionHandler> handlerClass);

    ConditionHandler getConditionHandler(String type);

    ActionHandler getActionHandler(String type);

    BulkActionHandler getBulkActionHandler(String type);
}
