package fintech.bo.components;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import fintech.Validate;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.webitel.WebitelAuthDialog;
import fintech.bo.components.webitel.WebitelAuthService;
import fintech.bo.components.webitel.api.WebitelApiClient;
import fintech.webitel.model.WebitelCallCommand;

public class WebitelButton extends Button {

    private WebitelApiClient webitelApiClient;

    public WebitelButton(WebitelApiClient webitelApiClient, String phone) {
        super(VaadinIcons.PHONE);
        this.webitelApiClient = webitelApiClient;
        setStyleName(ValoTheme.BUTTON_LINK);
        addStyleName(ValoTheme.BUTTON_ICON_ONLY);

        addClickListener((e) -> {
            if (WebitelAuthService.isLoggedIn()) {
                callWebitel(WebitelAuthService.getWebitelAuthData(), phone);
            } else {
                WebitelAuthDialog dialog =
                    new WebitelAuthDialog(webitelApiClient, webitelAuthData -> callWebitel(webitelAuthData, phone));
                UI.getCurrent().addWindow(dialog);
            }

        });

    }

    private void callWebitel(WebitelAuthService.WebitelData webitelAuthData, String phone) {
        Validate.notNull(webitelAuthData, "Null webitel data");
        WebitelCallCommand command = new WebitelCallCommand()
            .setCallFromUser(webitelAuthData.getUsername())
            .setDestinationNumber(phone)
            .setKey(webitelAuthData.getKey())
            .setToken(webitelAuthData.getToken());

        BackgroundOperations.callApi("Originating webitel call", webitelApiClient.call(command), r -> {
            Notifications.trayNotification("Webitel call started: " + r.getStatus());
        }, Notifications::errorNotification);
    }
}
