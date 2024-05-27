package fintech.bo.spain.alfa.client;

import com.vaadin.data.Binder;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.spain.alfa.bo.model.BlacklistClientRequest;

public class BlacklistClientDialog extends ActionDialog {

    private final AlfaApiClient clientApi;
    private final Binder<BlacklistClientRequest> binder = new Binder<>();

    public BlacklistClientDialog(ClientDTO client, AlfaApiClient clientApi) {
        super("Blacklist client", "Save");
        this.clientApi = clientApi;
        setDialogContent(form());
        setWidth(800, Unit.PIXELS);
        binder.setBean(new BlacklistClientRequest(client.getId(), ""));
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            BackgroundOperations.callApi("Updating data", clientApi.blacklistClient(binder.getBean()), t -> {
                Notifications.trayNotification("Updated");
                close();
            }, Notifications::errorNotification);
        }
    }

    private Component form() {
        FormLayout form = new FormLayout();
        form.setMargin(true);

        TextField comm = new TextField("Comment");
        comm.setWidth(100, Unit.PERCENTAGE);
        binder.forField(comm)
            .asRequired()
            .bind(BlacklistClientRequest::getComment, BlacklistClientRequest::setComment);
        form.addComponent(comm);

        return form;
    }
}
