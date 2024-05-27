package fintech.bo.components.payments;

import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;

public class AddTransactionDialog extends ActionDialog {

    private final AddTransactionComponent component;

    public AddTransactionDialog(PaymentRecord payment, AddTransactionComponent component) {
        super("Distribute payment", "Save");
        this.component = component;
        setDialogContent(component);
        setWidth(800, Unit.PIXELS);
        setModal(false);
    }

    @Override
    protected void executeAction() {
        component.saveCall().ifPresent(call -> {
            BackgroundOperations.callApi("Saving transaction", call, t -> {
                Notifications.trayNotification("Transaction saved");
                close();
            }, Notifications::errorNotification);
        });
    }
}
