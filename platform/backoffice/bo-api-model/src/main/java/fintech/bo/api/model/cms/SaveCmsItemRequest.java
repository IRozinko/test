package fintech.bo.api.model.cms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SaveCmsItemRequest {
    private String key;
    private String locale;
    private String emailSubjectTemplate;
    private String emailBodyTemplate;
    private String smsTextTemplate;
    private String contentTemplate;
    private String titleTemplate;
    private String headerTemplate;
    private String footerTemplate;
}
