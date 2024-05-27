package fintech.cms;

import fintech.cms.spi.CmsItem;

import java.util.Map;
import java.util.Optional;

public interface NotificationRenderer {

    Optional<CmsNotification> render(String templateKey, Map<String, Object> context, String locale);

    Optional<CmsNotification> render(CmsItem item, Map<String, Object> context, String locale);

    Optional<CmsNotification> buildTemplate(String templateKey, String locale);
}
