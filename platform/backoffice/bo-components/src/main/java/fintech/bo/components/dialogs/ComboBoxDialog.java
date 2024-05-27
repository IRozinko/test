package fintech.bo.components.dialogs;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.List;
import java.util.function.Consumer;

public class ComboBoxDialog extends Window {

    private final Button actionButton;

    private final ComboBox<String> comboBox;

    public ComboBoxDialog(String caption, String comboCaption, String actionButtonName, List<String> data, Consumer<String> onAction) {
        super(caption);

        comboBox = comboBox(comboCaption, data);
        actionButton = new Button(actionButtonName);

        comboBox.setWidth(100, Unit.PERCENTAGE);

        actionButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        actionButton.addClickListener((event) -> {
            onAction.accept(comboBox.getValue());
            close();
        });
        actionButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener((event) -> close());

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(cancelButton, actionButton);

        VerticalLayout bottom = new VerticalLayout();
        bottom.setMargin(false);
        bottom.addComponent(buttons);
        bottom.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);

        VerticalLayout content = new VerticalLayout();
        content.addComponent(comboBox);
        content.addComponent(bottom);
        setContent(content);
        setWidth(400, Unit.PIXELS);
        setModal(true);
        center();

    }

    public ComboBox<String> comboBox(String caption, List<String> data) {
        ComboBox<String> comboBox = new ComboBox<>(caption);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setItems(data);
        if (!data.isEmpty()) {
            comboBox.setValue(data.get(0));
        }
        comboBox.setPageLength(20);
        return comboBox;
    }

}
