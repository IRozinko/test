package fintech.bo.api.server.services

import fintech.cms.CmsContextBuilder
import org.springframework.stereotype.Component

@Component
class TestCmsContextBuilder implements CmsContextBuilder {

    @Override
    String companyLocale() {
        return null
    }

    @Override
    Map<String, Object> basicContext(Long clientId, Long debtId) {
        return null
    }

    @Override
    Map<String, Object> anonymousNotificationContext(Map<String, Object> context) {
        return null
    }

    @Override
    Map<String, Object> basicContext(Long clientId, Map<String, Object> context) {
        return null
    }
}
