package fintech.bo.spain.alfa.dc.batch;

import com.vaadin.ui.UI;
import fintech.bo.components.dialogs.ComboBoxDialog;

import java.util.List;
import java.util.function.Consumer;

public class EditDebtCompanyDialog {

    private ComboBoxDialog dialog;

    private EditDebtCompanyDialog(String caption, String actionButtonName, List<String> items, Consumer<String> onAction) {
        dialog = new ComboBoxDialog(caption, "Companies", actionButtonName, items, onAction);
        dialog.addCloseListener((e) -> {});
    }

    public void show() {
        UI.getCurrent().addWindow(dialog);
    }

    public static EditDebtCompanyDialog toSell(List<String> items, Consumer<String> onAction) {
        return new EditDebtCompanyDialog("Selling", "Sell", items, onAction);
    }

    public static EditDebtCompanyDialog toExternalize(List<String> data, Consumer<String> onAction) {
        return new EditDebtCompanyDialog("Externalizing", "Externalize", data, onAction);
    }
}
