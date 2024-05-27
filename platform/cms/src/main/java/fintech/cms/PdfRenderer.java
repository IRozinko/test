package fintech.cms;

import fintech.cms.spi.CmsItem;

import java.util.Map;
import java.util.Optional;

public interface PdfRenderer {

    Optional<Pdf> render(CmsItem item, Map<String, Object> context);

    Optional<Pdf> render(String templateKey, Map<String, Object> context, String locale);

    Pdf renderRequired(String templateKey, Map<String, Object> context, String locale);

    /**
     * if disable, returns empty byte[0] content
     * speeds up test considerably
     */
    void disableRendering(boolean disable);
}
