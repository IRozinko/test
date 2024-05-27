package fintech.bo.components.dc.batch;

import fintech.bo.components.dc.DcComponents;
import fintech.bo.components.dc.DcQueries;
import fintech.bo.components.dc.DebtStatusStateDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class EditDebtDialogFactory {

    @Autowired
    private DcComponents dcComponents;

    @Autowired
    private DcQueries dcQueries;

    public EditDebtDialog repurchase(Consumer<EditDebtDialog.EditDebtForm> consumer) {
        return new EditDebtDialog("Repurchase debts", "Repurchase", true, dcComponents, dcQueries, consumer);
    }
    public DebtStatusStateDialog editDebtStateAndStatus(Consumer<DebtStatusStateDialog.EditDebtStateForm> consumer) {
        return new DebtStatusStateDialog("Debt state and status", "edit debt", consumer);
    }

    public EditDebtDialog recoverExternal(Consumer<EditDebtDialog.EditDebtForm> consumer) {
        return new EditDebtDialog("Recover external debts", "Recover", false, dcComponents, dcQueries, consumer);
    }

    public EditDebtDialog recoverExternal(Consumer<EditDebtDialog.EditDebtForm> consumer, String defaultPortfolio) {
        return new EditDebtDialog("Recover external debts", "Recover", false,
            dcComponents, dcQueries, consumer, defaultPortfolio);
    }

    public EditDebtDialog reassign(Consumer<EditDebtDialog.EditDebtForm> consumer) {
        return new EditDebtDialog("Reassign debts", "Reassign", true, dcComponents, dcQueries, consumer);
    }
}
