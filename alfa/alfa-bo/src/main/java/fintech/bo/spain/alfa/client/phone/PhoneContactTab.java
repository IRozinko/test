package fintech.bo.spain.alfa.client.phone;

import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.client.PhoneGridDataProvider;
import fintech.bo.spain.alfa.api.PhoneContactApiClient;
import org.jooq.DSLContext;

import static fintech.bo.api.model.permissions.BackofficePermissions.CLIENT_PHONES_TAB_VIEW_AND_EDIT;
import static fintech.bo.components.security.LoginService.hasPermission;
import static fintech.bo.spain.alfa.client.phone.PhoneContactDialog.createPhone;

public class PhoneContactTab extends VerticalLayout {

    private final long clientId;
    private final PhoneContactApiClient api;
    private final DSLContext db;

    public PhoneContactTab(long clientId, DSLContext db, PhoneContactApiClient phoneContactApiClient) {
        super();
        this.clientId = clientId;
        this.api = phoneContactApiClient;
        this.db = db;
        render();
    }

    private void render() {
        removeAllComponents();
        addComponent(addPhoneButton());
        addComponent(new PhoneContactGrid(new PhoneGridDataProvider(clientId, db), api));
    }

    private Button addPhoneButton() {
        Button button = new Button("Add phone");
        button.setEnabled(hasPermission(CLIENT_PHONES_TAB_VIEW_AND_EDIT));
        button.addClickListener(event -> UI.getCurrent().addWindow(createPhone(clientId, api, this::render)));
        return button;
    }

}
