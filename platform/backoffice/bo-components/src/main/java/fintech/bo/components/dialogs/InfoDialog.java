package fintech.bo.components.dialogs;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class InfoDialog extends Window {

    public InfoDialog(String caption, Component dialogContent) {
        super(caption);

        Button closeButton = new Button("Close");
        closeButton.addClickListener((event) -> close());
        closeButton.focus();

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(closeButton);
        buttons.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

        VerticalLayout root = new VerticalLayout();
        root.addComponentsAndExpand(dialogContent);
        root.addComponent(buttons);
        root.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
        setContent(root);
        center();
        setWidth(400, Unit.PIXELS);
        setHeight(400, Unit.PIXELS);
    }
}
