package fintech.bo.components.loan;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.model.loan.BreakLoanRequest;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import retrofit2.Call;

public class BreakLoanDialog extends ActionDialog {

    private final Long loanId;

    public BreakLoanDialog(Long loanId) {
        super("", "Break");
        this.loanId = loanId;

        setDialogContent(new VerticalLayout(new Label("Break loan?")));
        setWidth(400, Unit.PIXELS);
    }

    @Override
    protected void executeAction() {
        BreakLoanRequest request = new BreakLoanRequest();
        request.setLoanId(loanId);

        Call<Void> call = ApiAccessor.gI().get(LoanApiClient.class).breakLoan(request);
        BackgroundOperations.callApi("Breaking loan", call,
            t -> {
                Notifications.trayNotification("Loan broken");
                close();
            },
            Notifications::errorNotification);
    }

}
