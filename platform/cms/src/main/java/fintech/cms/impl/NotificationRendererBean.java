package fintech.cms.impl;

import com.google.common.collect.ImmutableMap;
import fintech.cms.CmsNotification;
import fintech.cms.NotificationRenderer;
import fintech.cms.impl.pebble.PebbleTemplateEngine;
import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsItemType;
import fintech.cms.spi.CmsRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class NotificationRendererBean implements NotificationRenderer {

    @Autowired
    private CmsRegistry registry;

    @Autowired
    PebbleTemplateEngine pebbleTemplateEngine;

    @Override
    public Optional<CmsNotification> render(String templateKey, Map<String, Object> context, String locale) {
        Optional<CmsItem> item = registry.findItem(CmsItemType.NOTIFICATION, templateKey, locale);
        if (!item.isPresent()) {
            return Optional.empty();
        }
        Map<String, Object> finalContext = new HashMap<>();
        finalContext.putAll(registry.getDefaultContext());
        finalContext.putAll(context);
        ContextUtil.validateContext(templateKey, item.get().getScope(), finalContext);
        return Optional.of(renderCmsNotification(item.get(), finalContext, locale));
    }

    @Override
    public Optional<CmsNotification> render(CmsItem item, Map<String, Object> context, String locale) {
        Map<String, Object> finalContext = new HashMap<>();
        finalContext.putAll(registry.getDefaultContext());
        finalContext.putAll(context);
        return Optional.of(renderCmsNotification(item, finalContext, locale));
    }

    @Override
    public Optional<CmsNotification> buildTemplate(String templateKey, String locale) {
        Optional<CmsItem> maybeItem = registry.findItem(CmsItemType.NOTIFICATION, templateKey, locale);
        if (!maybeItem.isPresent()) {
            return Optional.empty();
        }
        CmsItem item = maybeItem.get();
        Map<String, Object> context = ImmutableMap.of();

        return Optional.of(renderCmsNotification(item, context, locale));
    }

    private CmsNotification renderCmsNotification(CmsItem item, Map<String, Object> context, String locale) {
        CmsNotification.Email email = renderEmail(item, context, locale);
        CmsNotification.Sms sms = renderSms(item, context, locale);
        return new CmsNotification(email, sms);
    }

    private CmsNotification.Sms renderSms(CmsItem template, Map<String, Object> context, String locale) {
        if (StringUtils.isBlank(template.getSmsTextTemplate())) {
            return null;
        }
        String text = pebbleTemplateEngine.render(template.getSmsTextTemplate(), context, locale);
        return new CmsNotification.Sms(text);
    }

    private CmsNotification.Email renderEmail(CmsItem template, Map<String, Object> context, String locale) {
        if (StringUtils.isBlank(template.getEmailBodyTemplate()) || StringUtils.isBlank(template.getEmailSubjectTemplate())) {
            return null;
        }
        String subject = pebbleTemplateEngine.render(template.getEmailSubjectTemplate(), context, locale);
        String body = pebbleTemplateEngine.render(template.getEmailBodyTemplate(), context, locale);
        return new CmsNotification.Email(subject, body);
    }

}
