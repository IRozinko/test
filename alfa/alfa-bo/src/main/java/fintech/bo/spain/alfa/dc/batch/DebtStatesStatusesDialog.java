package fintech.bo.spain.alfa.dc.batch;

import com.vaadin.ui.UI;
import fintech.bo.components.dialogs.ComboBoxDialog;

import java.util.List;
import java.util.function.Consumer;

public class DebtStatesStatusesDialog {

    private ComboBoxDialog stateDialog;
    private ComboBoxDialog statusDialog;

    private DebtStatesStatusesDialog(String caption, String actionButtonName,  List<String> states,Consumer<String> stateAction,List<String> statuses,Consumer<String> statusAction) {
        stateDialog = new ComboBoxDialog(caption, "States", actionButtonName, states, stateAction);
        statusDialog = new ComboBoxDialog(caption, "Statuses", actionButtonName, statuses, statusAction);
        stateDialog.addCloseListener((e) -> {});
        statusDialog.addCloseListener((e) -> {});
    }

    public void show() {
        UI.getCurrent().addWindow(stateDialog);
    }

    public static DebtStatesStatusesDialog toUpdate(List<String> states, List<String> statuses, Consumer<String> stateAction, Consumer<String> statusAction) {
        return new DebtStatesStatusesDialog("Updating debt state and status", "Update Debt", states,stateAction, statuses,statusAction);
    }

}
