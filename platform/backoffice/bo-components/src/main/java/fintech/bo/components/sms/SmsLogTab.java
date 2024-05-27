package fintech.bo.components.sms;

import com.vaadin.ui.Component;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.common.Tab;
import fintech.bo.components.security.SecuredTab;

@SecuredTab(permissions = BackofficePermissions.ADMIN, condition = "#client.deleted == true")
public class SmsLogTab extends Tab {

    private final SmsComponents smsComponents;

    public SmsLogTab(String caption, ClientDTO client, SmsComponents smsComponents) {
        super(caption, client);
        this.smsComponents = smsComponents;
    }

    @Override
    public Component build() {
        SmsLogDataProvider dataProvider = smsComponents.smsLogDataProvider();
        dataProvider.setClientId(client.getId());
        return smsComponents.smsLogGrid(dataProvider);
    }
}
