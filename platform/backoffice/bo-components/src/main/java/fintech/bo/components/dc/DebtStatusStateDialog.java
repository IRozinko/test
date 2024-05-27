package fintech.bo.components.dc;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Consumer;

public class DebtStatusStateDialog extends Window {

    public DebtStatusStateDialog(String caption, String actionButtonCaption, Consumer<EditDebtStateForm> consumer) {
        super(caption);
        ComboBox<String> statusesComboBox = statusesComboBox();
        statusesComboBox.setWidth(100, Unit.PERCENTAGE);
        statusesComboBox.addValueChangeListener(e -> statusesComboBox.setValue(e.getValue()));

        ComboBox<String> stateComboBox = statesComboBox();
        stateComboBox.setWidth(100, Unit.PERCENTAGE);
        stateComboBox.addValueChangeListener(e -> stateComboBox.setValue(e.getValue()));

        Button actionButton = new Button(actionButtonCaption);
        actionButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        actionButton.addClickListener((event) -> {
            consumer.accept(new DebtStatusStateDialog.EditDebtStateForm()
                .setStatus(statusesComboBox.getValue())
                .setState(stateComboBox.getValue()));
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
        content.addComponent(stateComboBox);
        content.addComponent(statusesComboBox);
        content.addComponent(bottom);
        setContent(content);
        setWidth(400, Unit.PIXELS);
        setModal(true);
        center();
    }


    private ComboBox<String> statusesComboBox() {
        List<String> items = DebtStatus.statusesList;
        ComboBox<String> comboBox = new ComboBox<>("Debt Status");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setPageLength(20);
        return comboBox;
    }
    private ComboBox<String> statesComboBox() {
        List<String> items = DebtState.statesList;
        ComboBox<String> comboBox = new ComboBox<>("Debt States");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setPageLength(20);
        return comboBox;
    }
    @Data
    @Accessors(chain = true)
    public static class EditDebtStateForm {
        private String status;
        private String state;
    }
}
