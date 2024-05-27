package fintech.cms;

import java.util.Map;

public interface CmsContextBuilder {

    String companyLocale();

    Map<String, Object> basicContext(Long clientId, Long debtId);

    Map<String, Object> anonymousNotificationContext(Map<String, Object> context);

    Map<String, Object> basicContext(Long clientId, Map<String, Object> context);

}
