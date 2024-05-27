package fintech.spain.web.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsItemSavedEvent;
import fintech.cms.spi.CmsItemType;
import fintech.cms.spi.CmsRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.ResourceLoader;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CmsBasedMessageSource extends AbstractMessageSource implements ResourceLoaderAware {

    private CmsRegistry cmsRegistry;

    private final String key;
    private final String locale;

    private Supplier<Map<String, String>> propertiesCache = buildPropertiesCache();

    public CmsBasedMessageSource(String key, String locale) {
        this.key = key;
        this.locale = locale;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        Map<String, String> properties = propertiesCache.get();
        String value = properties.get(code);
        if (value == null) {
            value = properties.get(code.toLowerCase());
        }
        if (value == null) {
            return createMessageFormat("", locale);
        }
        return createMessageFormat(value, locale);
    }

    private Supplier<Map<String, String>> buildPropertiesCache() {
        return Suppliers.memoizeWithExpiration(this::reload, 1, TimeUnit.MINUTES);
    }

    private Map<String, String> reload() {
        log.info("Reloading localization properties");
        Optional<CmsItem> item = cmsRegistry.findItem(CmsItemType.TRANSLATION, key, locale);
        if (!item.isPresent()) {
            log.warn("Localization not configured in CMS");
            return ImmutableMap.of();
        }
        return JsonUtils.readValue(item.get().getContentTemplate(), new TypeReference<Map<String, String>>() {
        });
    }

    @EventListener
    public void onCmsItemSaved(CmsItemSavedEvent event) {
        if (key.equals(event.getItem().getKey())) {
            // validate
            Map<String, String> newValue = JsonUtils.readValue(event.getItem().getContentTemplate(), new TypeReference<Map<String, String>>() {
            });
            Validate.isTrue(!newValue.isEmpty(), "Empty localization");
            this.propertiesCache = buildPropertiesCache();
        }
    }

    @Autowired
    public void setCmsRegistry(CmsRegistry cmsRegistry) {
        this.cmsRegistry = cmsRegistry;
    }
}
