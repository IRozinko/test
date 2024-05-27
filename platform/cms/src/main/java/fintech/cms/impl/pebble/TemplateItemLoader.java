package fintech.cms.impl.pebble;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.Loader;
import fintech.Validate;
import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsItemType;
import fintech.cms.spi.CmsRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TemplateItemLoader implements Loader<String> {

    private final CmsRegistry registry;

    private static final ThreadLocal<String> LOCALE = ThreadLocal.withInitial(() -> "es");

    @Override
    public Reader getReader(String cacheKey) throws LoaderException {
        if (!StringUtils.startsWith(cacheKey, "_")) {
            throw new LoaderException(null, "Template not found: " + cacheKey);
        }
        Optional<CmsItem> item = registry.findItem(CmsItemType.EMBEDDABLE, cacheKey, LOCALE.get());
        Validate.isTrue(item.isPresent(), "Template item not found by key [%s]", cacheKey);
        String contentTemplate = item.get().getContentTemplate();
        Validate.notBlank(contentTemplate, "Empty content in item [%s]", cacheKey);
        return new StringReader(contentTemplate);
    }

    @Override
    public void setCharset(String charset) {

    }

    @Override
    public void setPrefix(String prefix) {

    }

    @Override
    public void setSuffix(String suffix) {

    }

    @Override
    public String resolveRelativePath(String relativePath, String anchorPath) {
        return null;
    }

    @Override
    public String createCacheKey(String templateName) {
        return templateName;
    }

    public void setLocale(String locale) {
        LOCALE.set(locale);
    }

}
