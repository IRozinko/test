package fintech.bo.components.security;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.common.Action;

public class LoginComponent extends CustomComponent {

    private final Action loginAction;

    public LoginComponent(Action loginAction) {
        this.loginAction = loginAction;
        setCompositionRoot(buildLoginUi());
    }

    private Component buildLoginUi() {
        Label title = new Label("Backoffice Application");
        title.addStyleName(ValoTheme.LABEL_H3);

        Button login = new Button("Login");
        login.addStyleName(ValoTheme.BUTTON_PRIMARY);
        login.addClickListener(event -> loginAction.execute());

        VerticalLayout center = new VerticalLayout();
        center.setSpacing(true);
        center.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        center.addComponents(title, login);
        center.setSizeFull();

        Panel panel = new Panel(center);
        panel.setSizeUndefined();
        panel.setWidth(300, Sizeable.Unit.PIXELS);
        panel.setHeight(150, Sizeable.Unit.PIXELS);

        Label spacer = new Label();
        spacer.setHeight(100, Sizeable.Unit.PIXELS);

        VerticalLayout root = new VerticalLayout();
        root.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        root.setMargin(true);
        root.setSpacing(true);
        root.addComponent(spacer);
        root.addComponent(panel);
        return root;
    }

}
