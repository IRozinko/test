package fintech.bo.components.users;

import com.vaadin.data.Binder;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.api.client.RolesApiClient;
import fintech.bo.api.model.permissions.SaveRoleRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;

import java.util.List;

import static com.vaadin.data.ValidationResult.error;
import static com.vaadin.data.ValidationResult.ok;

public class AddRoleDialog extends ActionDialog {

    private RolesApiClient rolesApiClient;
    private List<String> restrictedNames;
    private Binder<SaveRoleRequest> binder;
    private SaveRoleRequest request;

    AddRoleDialog(RolesApiClient rolesApiClient, List<String> restrictedNames) {
        super("Add role", "Save");
        this.rolesApiClient = rolesApiClient;
        this.restrictedNames = restrictedNames;
        setModal(true);
        setWidth(400, Unit.PIXELS);
        setDialogContent(buildForm());
    }

    private Component buildForm() {
        request = new SaveRoleRequest();
        binder = new Binder<>();
        binder.setBean(request);
        FormLayout form = new FormLayout();
        form.setWidthUndefined();
        form.addComponent(getInput());
        form.setSizeFull();
        return form;
    }

    private Component getInput() {
        TextField input = new TextField("Name");
        input.setWidth(100, Unit.PERCENTAGE);
        input.focus();

        binder.forField(input)
            .asRequired("Name should be specified")
            .withValidator((value, context) -> restrictedNames.contains(value) ? error("Role exists with such name") : ok())
            .bind(SaveRoleRequest::getName, SaveRoleRequest::setName);

        return input;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            BackgroundOperations.callApi("Saving role", rolesApiClient.update(request), t -> {
                Notifications.trayNotification("Role saved");
                close();
            }, Notifications::errorNotification);
        }
    }

}
