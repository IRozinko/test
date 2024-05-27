package fintech.bo.components.dialogs;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class ErrorDialog extends Window {

    private static final int COLLAPSED_WIDTH = 500;
    private static final int COLLAPSED_HEIGHT = 120;

    private static final int EXPANDED_WIDTH = 500;
    private static final int EXPANDED_HEIGHT = 500;

    public ErrorDialog(String caption, String stacktrace) {
        super();
        setModal(true);
        setStyleName("error-dialog");

        TextArea textArea = new TextArea();
        textArea.setReadOnly(true);
        textArea.setVisible(false);
        textArea.setValue(stacktrace);
        textArea.setRows(10);
        textArea.setSizeFull();

        Label label = new Label(caption);
        label.setWidth(100, Unit.PERCENTAGE);

        Button detailsButton = new Button("Details");
        detailsButton.addStyleName(ValoTheme.BUTTON_DANGER);
        detailsButton.addClickListener((event) -> expand(textArea));

        Button closeButton = new Button("Close");
        closeButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        closeButton.addClickListener(e -> close());

        VerticalLayout content = new VerticalLayout();
        content.addComponent(new VerticalLayout(label));
        HorizontalLayout buttons = new HorizontalLayout(detailsButton, closeButton);
        content.addComponent(buttons);
        content.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
        content.addComponentsAndExpand(textArea);
        content.setSizeUndefined();
        content.setWidth(100, Unit.PERCENTAGE);
        setContent(content);
        center();
        setWidth(COLLAPSED_WIDTH, Unit.PIXELS);

        addCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        addCloseShortcut(ShortcutAction.KeyCode.ENTER);
    }

    private void expand(TextArea textArea) {
        textArea.setVisible(!textArea.isVisible());
    }
}
