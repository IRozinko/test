package fintech.bo.components.views;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class PermissionDeniedComponent extends VerticalLayout implements BoComponent {

    @Override
    public void setUp(BoComponentContext context) {
        Label label = new Label("Permission denied");
        label.addStyleName(ValoTheme.LABEL_FAILURE);
        addComponent(label);
    }

    @Override
    public void refresh() {
    }
}
