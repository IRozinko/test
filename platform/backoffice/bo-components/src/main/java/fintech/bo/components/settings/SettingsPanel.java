package fintech.bo.components.settings;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;


public class SettingsPanel extends Panel {

    private FormLayout layout;

    public SettingsPanel(String caption) {
        super(caption);
        layout = new FormLayout();
        layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        layout.setMargin(true);
        setContent(layout);
    }

    public void addComponent(Component c) {
        layout.addComponent(c);
    }
}
