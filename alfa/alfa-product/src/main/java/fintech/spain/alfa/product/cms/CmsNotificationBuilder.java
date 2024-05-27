package fintech.spain.alfa.product.cms;

import fintech.Validate;
import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsItemType;
import fintech.spain.alfa.product.AlfaConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmsNotificationBuilder {

    private final String key;
    private List<String> scopes = new ArrayList<>();
    private final CmsItem item = new CmsItem();

    CmsNotificationBuilder(String key) {
        Validate.notBlank(key, "Empty cms key");
        this.key = key;
        this.scopes.add(AlfaCmsModels.SCOPE_COMPANY);
    }

    public CmsNotificationBuilder scopes(String... scopes) {
        this.scopes.addAll(Arrays.asList(scopes));
        return this;
    }

    public CmsNotificationBuilder description(String description) {
        this.item.setDescription(description);
        return this;
    }

    public CmsNotificationBuilder email(String subject, String body) {
        Validate.notBlank(subject, "Empty email subject");
        Validate.notBlank(body, "Empty email body");
        item.setEmailSubjectTemplate(subject);
        item.setEmailBodyTemplate(body);
        return this;
    }

    public CmsNotificationBuilder sms(String text) {
        Validate.notBlank(text, "Empty SMS text");
        item.setSmsTextTemplate(text);
        return this;
    }

    CmsItem build() {
        item.setItemType(CmsItemType.NOTIFICATION);
        if (item.getDescription() == null) {
            item.setDescription("");
        }
        item.setScope(String.join(",", this.scopes));
        item.setKey(this.key);
        item.setLocale(AlfaConstants.LOCALE);
        return item;
    }
}
