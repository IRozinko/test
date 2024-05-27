package fintech.bo.api.model.cms;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class UpdateCmsItemRequest {

    private Map<String, CmsItem> items = new HashMap<>();

    @Data
    @Accessors(chain = true)
    public static class CmsItem {
        private String key;
        private String locale;
        private String description;
        private String scope;
        private String itemType;
        private String emailSubjectTemplate;
        private String emailBodyTemplate;
        private String smsTextTemplate;
        private String contentTemplate;
        private String titleTemplate;
        private String headerTemplate;
        private String footerTemplate;
    }

}

