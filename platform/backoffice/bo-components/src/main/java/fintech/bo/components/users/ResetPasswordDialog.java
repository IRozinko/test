package fintech.bo.components.users;

import com.vaadin.data.Binder;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.api.client.UsersApiClient;
import fintech.bo.api.model.users.ChangePasswordRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import org.apache.commons.lang3.RandomStringUtils;

public class ResetPasswordDialog extends ActionDialog {

    private UsersApiClient usersApiClient;
    private Binder<ChangePasswordRequest> binder;
    private ChangePasswordRequest request;

    public ResetPasswordDialog(String email, UsersApiClient usersApiClient) {
        super("Reset password", "Reset");
        this.usersApiClient = usersApiClient;
        request = new ChangePasswordRequest();
        request.setEmail(email);
        request.setPassword(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
        request.setTemporaryPassword(true);
        setModal(true);
        setWidth(400, Unit.PIXELS);
        setDialogContent(buildForm());
    }

    private Component buildForm() {
        binder = new Binder<>();
        binder.setBean(request);

        FormLayout form = new FormLayout();
        form.setWidthUndefined();

        TextField password = new TextField("Password");
        password.setWidth(100, Unit.PERCENTAGE);
        password.selectAll();
        password.focus();
        binder.forField(password).bind(ChangePasswordRequest::getPassword, ChangePasswordRequest::setPassword);
        form.addComponent(password);

        form.setSizeFull();
        return form;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            BackgroundOperations.callApi("Resetting password", usersApiClient.changePassword(request), t -> {
                Notifications.trayNotification("Password reset completed");
                close();
            }, Notifications::errorNotification);
        }
    }

}
