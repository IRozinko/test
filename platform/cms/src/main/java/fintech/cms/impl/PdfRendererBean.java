package fintech.cms.impl;

import fintech.cms.Pdf;
import fintech.cms.PdfRenderer;
import fintech.cms.impl.itext.ITextPdfHelper;
import fintech.cms.impl.pebble.PebbleTemplateEngine;
import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsItemType;
import fintech.cms.spi.CmsRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.isTrue;

@Slf4j
@Component
public class PdfRendererBean implements PdfRenderer {

    @Autowired
    private CmsRegistry cmsRegistry;

    @Autowired
    PebbleTemplateEngine pebbleTemplateEngine;

    private boolean disabled;

    @Override
    public Optional<Pdf> render(CmsItem item, Map<String, Object> context) {
        if (StringUtils.isBlank(item.getTitleTemplate())) {
            return Optional.empty();
        }
        if (StringUtils.isBlank(item.getContentTemplate())) {
            return Optional.empty();
        }

        Map<String, Object> finalContext = new HashMap<>();
        finalContext.putAll(cmsRegistry.getDefaultContext());
        finalContext.putAll(context);

        ContextUtil.validateContext(item.getKey(), item.getScope(), finalContext);
        String name = pebbleTemplateEngine.render(item.getTitleTemplate(), finalContext, item.getLocale());

        if (disabled) {
            log.warn("PDF rendering disabled, returning empty content");
            return Optional.of(new Pdf(name, new byte[]{0}));
        }

        String html = item.getContentTemplate();
        String finalHtmlCode = pebbleTemplateEngine.render(html, finalContext, item.getLocale());
        String headerValue = null;
        String footerValue = null;
        if (item.getHeaderTemplate() != null) {
            headerValue = pebbleTemplateEngine.render(item.getHeaderTemplate(), finalContext, item.getLocale());
        }
        if (item.getFooterTemplate() != null) {
            footerValue = pebbleTemplateEngine.render(item.getFooterTemplate(), finalContext, item.getLocale());
        }
        byte[] content = ITextPdfHelper.generatePdf(finalHtmlCode, headerValue, footerValue);
        return Optional.of(new Pdf(name, content));
    }

    @Override
    public Optional<Pdf> render(String templateKey, Map<String, Object> context, String locale) {
        Optional<CmsItem> item = cmsRegistry.findItem(CmsItemType.PDF_HTML, templateKey, locale);
        if (!item.isPresent()) {
            return Optional.empty();
        }
        return render(item.get(), context);
    }

    @Override
    public Pdf renderRequired(String templateKey, Map<String, Object> context, String locale) {
        Optional<Pdf> pdf = render(templateKey, context, locale);
        isTrue(pdf.isPresent(), "[%s] PDF not generated", templateKey);
        return pdf.get();
    }

    @Override
    public void disableRendering(boolean disable) {
        this.disabled = disable;
    }
}
