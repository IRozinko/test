package fintech.bo.components.dialogs;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

public class TextInputDialog extends Window {

    private final Button actionButton;
    protected final AbstractTextField input;

    public TextInputDialog(String caption, String inputCaption, String actionButtonName, Consumer<String> onAction) {
        super(caption);

        input = getInput(inputCaption);
        actionButton = new Button(actionButtonName);

        input.setWidth(100, Unit.PERCENTAGE);
        input.addValueChangeListener(e -> actionButton.setEnabled(!StringUtils.isBlank(e.getValue())));
        input.focus();

        actionButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        actionButton.addClickListener((event) -> {
            onAction.accept(input.getValue());
            close();
        });
        actionButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        actionButton.setEnabled(false);

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener((event) -> close());

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(cancelButton, actionButton);

        VerticalLayout bottom = new VerticalLayout();
        bottom.setMargin(false);
        bottom.addComponent(buttons);
        bottom.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);

        VerticalLayout content = new VerticalLayout();
        content.addComponent(input);
        content.addComponent(bottom);
        setContent(content);
        setWidth(400, Unit.PIXELS);
        setModal(true);
        center();
    }

    protected AbstractTextField getInput(String caption) {
        return new TextField(caption);
    }
}
