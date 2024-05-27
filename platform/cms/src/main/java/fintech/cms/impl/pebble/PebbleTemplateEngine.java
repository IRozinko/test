package fintech.cms.impl.pebble;

import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import fintech.cms.spi.CmsItemDeleteEvent;
import fintech.cms.spi.CmsItemSavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class PebbleTemplateEngine {

    private final TemplateItemLoader templateItemLoader;

    private final Supplier<PebbleEngine> engine = Suppliers.memoize(this::buildEngine)::get;
    private final Supplier<PebbleEngine> simpleEngine = Suppliers.memoize(this::buildSimpleEngine)::get;

    private PebbleEngine buildEngine() {
        templateItemLoader.setLocale("en");
        return new PebbleEngine.Builder()
            .templateCache(CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(1, TimeUnit.MINUTES).build())
            .tagCache(CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(1, TimeUnit.MINUTES).build())
            .loader(new DelegatingLoader(ImmutableList.of(new ClasspathLoader(), templateItemLoader, new StringLoader())))
            .extension(new CustomExtension())
            .build();
    }

    private PebbleEngine buildSimpleEngine() {
        return new PebbleEngine.Builder()
            .templateCache(CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(1, TimeUnit.MINUTES).build())
            .tagCache(CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(1, TimeUnit.MINUTES).build())
            .loader(new StringLoader())
            .extension(new CustomExtension())
            .build();
    }

    @SneakyThrows
    public String render(String template, Map<String, Object> context, String locale) {
        StringWriter sw = new StringWriter();
        templateItemLoader.setLocale(locale);
        engine.get().getTemplate(template).evaluate(sw, context, Locale.forLanguageTag(locale));
        return sw.toString();
    }

    @SneakyThrows
    public String renderString(String template, Map<String, Object> context, Locale locale) {
        StringWriter sw = new StringWriter();
        simpleEngine.get().getTemplate(template).evaluate(sw, context, locale);
        return sw.toString();
    }

    @EventListener
    public void onCmsItemSaved(CmsItemSavedEvent event) {
        invalidateCache();
    }

    @EventListener
    public void onCmsItemDeleted(CmsItemDeleteEvent event) {
        invalidateCache();
    }

    public void invalidateCache() {
        this.engine.get().getTemplateCache().invalidateAll();
        this.engine.get().getTagCache().invalidateAll();
    }
}
