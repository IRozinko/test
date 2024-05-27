package fintech.activity.spi;

public interface ActivityRegistry {

    void registerBulkActionHandler(String type, Class<? extends BulkActionHandler> handlerClass);

    BulkActionHandler getBulkActionHandler(String type);
}
