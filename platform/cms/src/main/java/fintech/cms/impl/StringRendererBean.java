package fintech.cms.impl;

import fintech.cms.StringRenderer;
import fintech.cms.impl.pebble.PebbleTemplateEngine;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class StringRendererBean implements StringRenderer {

    private final PebbleTemplateEngine pebbleTemplateEngine;

    public StringRendererBean(PebbleTemplateEngine pebbleTemplateEngine) {
        this.pebbleTemplateEngine = pebbleTemplateEngine;
    }

    @Override
    public String render(String template, Map<String, Object> context) {
        return pebbleTemplateEngine.renderString(template, context, Locale.forLanguageTag("default"));
    }
}
