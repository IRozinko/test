package fintech.bo.components.transaction;

import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.TransactionApiClient;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.transaction.VoidTransactionRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import org.jooq.Record;
import retrofit2.Call;

import static fintech.bo.db.jooq.transaction.tables.Transaction.TRANSACTION_;

public class TransactionInfoDialog extends Window {

    private TransactionApiClient transactionApiClient;
    private Record transaction;

    public TransactionInfoDialog(TransactionApiClient transactionApiClient, TransactionComponents transactionComponents, Record transaction) {
        super("Transaction");
        this.transactionApiClient = transactionApiClient;
        this.transaction = transaction;

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.addComponent(new Panel(transactionComponents.transactionInfo(transaction)));

        if (LoginService.hasPermission(BackofficePermissions.TRANSACTION_VOID) && !transaction.get(TRANSACTION_.VOIDED)) {
            Button voidButton = new Button("Void Transaction");
            voidButton.addStyleName(ValoTheme.BUTTON_DANGER);
            voidButton.addClickListener(e -> voidTransaction());
            layout.addComponent(voidButton);
        }

        setContent(layout);
        setWidth(400, Unit.PIXELS);
        center();
    }

    private void voidTransaction() {
        Dialogs.confirm("Void transaction?", (Button.ClickListener) event -> {
            VoidTransactionRequest request = new VoidTransactionRequest();
            request.setTransactionId(transaction.get(TRANSACTION_.ID));
            Call<Void> call = transactionApiClient.voidTransaction(request);
            BackgroundOperations.callApi("Voiding transaction", call, t -> {
                Notifications.trayNotification("Transaction voided");
                close();
            }, Notifications::errorNotification);
        });
    }
}
