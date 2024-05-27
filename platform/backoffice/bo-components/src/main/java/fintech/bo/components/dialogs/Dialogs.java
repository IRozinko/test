package fintech.bo.components.dialogs;

import com.google.common.base.MoreObjects;
import com.vaadin.data.HasValue;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import fintech.bo.components.BackofficeTheme;

public class Dialogs {

    public static InfoDialog showText(String title, String text) {
        TextArea textArea = new TextArea();
        textArea.setReadOnly(true);
        textArea.setValue(MoreObjects.firstNonNull(text, ""));
        textArea.setSizeFull();
        textArea.addStyleName(BackofficeTheme.TEXT_MONO);

        InfoDialog dialog = new InfoDialog(title, textArea);
        UI.getCurrent().addWindow(dialog);
        return dialog;
    }

    public static void confirm(String text, Button.ClickListener listener) {
        ConfirmDialog dialog = new ConfirmDialog(text, listener);
        UI.getCurrent().addWindow(dialog);
    }

    public static InfoDialog preview(String title, Component component) {
        if (component instanceof HasValue) {
            ((HasValue) component).setReadOnly(true);
        }
        InfoDialog dialog = new InfoDialog(title, component);
        UI.getCurrent().addWindow(dialog);
        return dialog;
    }
}

