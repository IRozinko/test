package fintech.bo.components.dialogs;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.common.Action;

public abstract class ActionDialog extends Window {

    private final VerticalLayout content;

    private Action action;

    private final Button actionButton;

    private final Button cancelButton;

    public ActionDialog(String caption, String actionButtonName) {
        super(caption);

        actionButton = new Button(actionButtonName);
        actionButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        actionButton.addClickListener((event) -> executeAction());
        actionButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        cancelButton = new Button("Cancel");
        cancelButton.addClickListener((event) -> close());

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(cancelButton, actionButton);

        VerticalLayout bottom = new VerticalLayout();
        bottom.setMargin(false);
        bottom.addComponent(buttons);
        bottom.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);

        content = new VerticalLayout();
        content.addComponent(bottom);
        setContent(content);
        center();
        setModal(true);
    }

    protected void setCancelButtonCaption(String caption) {
        cancelButton.setCaption(caption);
    }

    protected void executeAction() {
        action.execute();
    }

    public void setAction(Action action) {
        this.action = action;
    }

    protected void setDialogContent(Component component) {
        component.setSizeFull();
        content.addComponent(component, 0);
        content.setExpandRatio(component, 1.0f);
    }

    protected void fullHeight() {
        content.setSizeFull();
        setHeight(90, Unit.PERCENTAGE);
    }
    protected void disableActionButton() {
        actionButton.setEnabled(false);
    }

    public void removeClickShortcut() {
        actionButton.removeClickShortcut();
    }

}
