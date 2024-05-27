package fintech.bo.api.model.cms;

import lombok.Data;

@Data
public class RenderCmsItemRequest {
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
