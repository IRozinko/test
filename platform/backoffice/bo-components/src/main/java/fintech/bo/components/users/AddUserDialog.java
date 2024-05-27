package fintech.bo.components.users;

import com.vaadin.data.Binder;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.api.client.UsersApiClient;
import fintech.bo.api.model.users.AddUserRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

public class AddUserDialog extends ActionDialog {

    private UsersApiClient usersApiClient;
    private List<String> roles;
    private Binder<AddUserRequest> binder;
    private AddUserRequest request;

    public AddUserDialog(UsersApiClient usersApiClient, List<String> roles) {
        super("Add user", "Save");
        this.usersApiClient = usersApiClient;
        this.roles = roles;
        setModal(true);
        setWidth(400, Unit.PIXELS);
        setDialogContent(buildForm());
    }

    private Component buildForm() {
        request = new AddUserRequest();
        request.setPassword(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
        request.setTemporaryPassword(true);
        binder = new Binder<>();
        binder.setBean(request);

        FormLayout form = new FormLayout();
        form.setWidthUndefined();

        TextField email = new TextField("Email");
        email.setWidth(100, Unit.PERCENTAGE);
        email.focus();
        binder.forField(email).bind(AddUserRequest::getEmail, AddUserRequest::setEmail);
        form.addComponent(email);

        TextField password = new TextField("Password");
        password.setWidth(100, Unit.PERCENTAGE);
        binder.forField(password).bind(AddUserRequest::getPassword, AddUserRequest::setPassword);
        form.addComponent(password);

        roles.forEach((role) -> {
            CheckBox checkBox = new CheckBox(role);
            form.addComponent(checkBox);
            binder.bind(checkBox,
                request -> request.getRoles().contains(role),
                (request, value) -> {
                    if (value) {
                        request.getRoles().add(role);
                    } else {
                        request.getRoles().remove(role);
                    }
                });
        });

        form.setSizeFull();
        return form;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            BackgroundOperations.callApi("Adding user", usersApiClient.addUser(request), t -> {
                Notifications.trayNotification("User added");
                close();
            }, Notifications::errorNotification);
        }
    }

}
