package fintech.bo.components.security;

import com.google.common.base.MoreObjects;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.LoginApiClient;
import fintech.bo.api.model.LoginRequest;
import fintech.bo.api.model.LoginResponse;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

@Slf4j
@SpringComponent
public class SecurityComponents {

    private final LoginApiClient loginApiClient;
    private final LoginService loginService;

    @Autowired
    public SecurityComponents(LoginApiClient loginApiClient, LoginService loginService) {
        this.loginApiClient = loginApiClient;
        this.loginService = loginService;
    }

    public Component buildLoginUi(String title) {
        Label titleLabel = new Label(title);
        titleLabel.addStyleName(ValoTheme.LABEL_H3);

        final TextField email = new TextField("Email");
        email.setWidth(100, Sizeable.Unit.PERCENTAGE);
        final PasswordField password = new PasswordField("Password");
        password.setWidth(100, Sizeable.Unit.PERCENTAGE);

        Button login = new Button("Login");
        login.addStyleName(ValoTheme.BUTTON_PRIMARY);
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        login.addClickListener(event -> login(email.getValue(), password.getValue()));

        FormLayout center = new FormLayout();
        center.setMargin(true);
        center.setSpacing(true);
        center.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        center.addComponents(titleLabel, email, password, login);
        center.setSizeFull();

        Panel panel = new Panel(center);
        panel.setSizeUndefined();
        panel.setWidth(400, Sizeable.Unit.PIXELS);
        panel.setHeight(250, Sizeable.Unit.PIXELS);

        VerticalLayout root = new VerticalLayout();
        root.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        root.setMargin(false);
        root.setSpacing(true);
        root.addComponent(panel);
        root.setSizeFull();
        root.addStyleName(BackofficeTheme.LOGIN_SCREEN);
        return root;
    }

    public void login(String email, String password) {
        Call<LoginResponse> call = loginApiClient.login(new LoginRequest(email, password));
        BackgroundOperations.callApi("Logging in", call, response -> {
            log.info("Login success, user {}", response.getUser());
            loginService.login(response);
            String initialUriFragment = loginService.getInitialUriFragment();
            UI.getCurrent().getPage().setLocation(String.format("/#%s", MoreObjects.firstNonNull(initialUriFragment, "")));
            UI.getCurrent().getPage().reload();
        }, Notifications::errorNotification);
    }
}
