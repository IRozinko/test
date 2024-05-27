package fintech.cms.spi;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface CmsRegistry {

    Long saveItem(CmsItem item, boolean overwrite);

    Optional<CmsItem> findItem(CmsItemType type, String key, String locale);

    Optional<CmsItem> findItem(String key, String locale);

    void setTestingContext(Map<String, Supplier<Object>> testingContext);

    Map<String, Object> getTestingContext();

    void setDefaultContext(Map<String, Object> defaultContext);

    Map<String, Object> getDefaultContext();

    String getTestingContextDocumentation();

    void deleteItem(String key);

    void addLocale(String locale);

    void deleteLocale(String locale);

}
