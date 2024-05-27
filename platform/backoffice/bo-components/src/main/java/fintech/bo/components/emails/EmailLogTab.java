package fintech.bo.components.emails;

import com.vaadin.ui.Component;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.common.Tab;
import fintech.bo.components.security.SecuredTab;

@SecuredTab(permissions = BackofficePermissions.ADMIN, condition = "#client.deleted == true")
public class EmailLogTab extends Tab {

    private final EmailsComponents emailsComponents;

    public EmailLogTab(String caption, ClientDTO client, EmailsComponents emailsComponents) {
        super(caption, client);
        this.emailsComponents = emailsComponents;
    }

    @Override
    public Component build() {
        EmailLogDataProvider dataProvider = emailsComponents.emailLogDataProvider();
        dataProvider.setClientId(client.getId());
        return emailsComponents.emailLogGrid(dataProvider);
    }
}
