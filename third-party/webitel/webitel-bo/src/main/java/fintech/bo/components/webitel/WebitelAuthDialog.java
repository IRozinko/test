package fintech.bo.components.webitel;

import com.vaadin.data.Binder;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.webitel.api.WebitelApiClient;
import fintech.webitel.model.WebitelLoginCommand;

import java.util.function.Consumer;

public class WebitelAuthDialog extends ActionDialog {

    private Binder<WebitelLoginCommand> binder;
    private WebitelApiClient apiClient;
    private Consumer<WebitelAuthService.WebitelData> onLoginSuccessCallback;

    public WebitelAuthDialog(WebitelApiClient apiClient, Consumer<WebitelAuthService.WebitelData> onLoginSuccessCallback) {
        super("Webitel authorization", "Login");
        this.apiClient = apiClient;
        this.onLoginSuccessCallback = onLoginSuccessCallback;

        setModal(true);
        setWidth(400, Unit.PIXELS);
        setDialogContent(buildForm());
    }

    private Component buildForm() {
        binder = new Binder<>();
        binder.setBean(new WebitelLoginCommand());

        FormLayout form = new FormLayout();
        form.setWidthUndefined();

        TextField usernameField = new TextField("Username");
        usernameField.setWidth(100, Unit.PERCENTAGE);
        binder.forField(usernameField).bind(WebitelLoginCommand::getUsername, WebitelLoginCommand::setUsername);
        form.addComponent(usernameField);

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidth(100, Unit.PERCENTAGE);
        binder.forField(passwordField).bind(WebitelLoginCommand::getPassword, WebitelLoginCommand::setPassword);
        form.addComponent(passwordField);

        form.setSizeFull();

        usernameField.focus();

        return form;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            BackgroundOperations.callApi("Logging in webitel", apiClient.login(binder.getBean()), r -> {
                WebitelAuthService.saveLogin(r);
                onLoginSuccessCallback.accept(WebitelAuthService.getWebitelAuthData());
                close();
            }, Notifications::errorNotification);
        }
    }
}
