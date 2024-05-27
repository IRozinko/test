package fintech.bo.components.dialogs;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class ConfirmDialog extends Window {

    public ConfirmDialog(String caption, Button.ClickListener confirmListener) {
        super();
        setModal(true);

        Button actionButton = new Button("Ok");
        actionButton.setWidth(80, Unit.PIXELS);
        actionButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        actionButton.addClickListener((e) -> close());
        actionButton.addClickListener(confirmListener);
        actionButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener((event) -> close());

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(cancelButton, actionButton);

        VerticalLayout bottom = new VerticalLayout();
        bottom.setMargin(false);
        bottom.addComponent(buttons);
        bottom.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);

        Label label = new Label(caption);
        VerticalLayout labelLayout = new VerticalLayout();
        labelLayout.addComponent(label);
        labelLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

        VerticalLayout content = new VerticalLayout();
        content.addComponentsAndExpand(labelLayout);
        content.addComponent(bottom);
        setContent(content);
        center();
        setWidth(600, Unit.PIXELS);
        setHeight(150, Unit.PIXELS);
    }
}
