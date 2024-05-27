package fintech.cms.spi;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CmsItem {

    private String key;
    private String locale;
    private String description;
    private String scope;
    private CmsItemType itemType;
    private String emailSubjectTemplate;
    private String emailBodyTemplate;
    private String smsTextTemplate;
    private String contentTemplate;
    private String titleTemplate;
    private String headerTemplate;
    private String footerTemplate;

    public void setScopes(String... scopes) {
        this.scope = String.join(",", scopes);
    }
}
