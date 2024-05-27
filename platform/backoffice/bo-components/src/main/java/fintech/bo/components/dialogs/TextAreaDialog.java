package fintech.bo.components.dialogs;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import java.util.function.Consumer;

public class TextAreaDialog extends TextInputDialog {

    public TextAreaDialog(String caption, String inputCaption, String actionButtonName, Consumer<String> onAction) {
        super(caption, inputCaption, actionButtonName, onAction);
    }

    @Override
    protected AbstractTextField getInput(String caption) {
        return new TextArea(caption);
    }
}
