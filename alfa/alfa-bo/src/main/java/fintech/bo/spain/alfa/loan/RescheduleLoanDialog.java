package fintech.bo.spain.alfa.loan;

import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.dc.RescheduleLoanRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.spain.alfa.api.AlfaApiClient;

import java.util.Optional;

class RescheduleLoanDialog extends ActionDialog {

    private final Long loanId;
    private final AlfaApiClient apiClient;
    private final Label errorLabel;
    private final DebtReschedulingComponent debtReschedulingComponent;

    RescheduleLoanDialog(Long loanId, AlfaApiClient apiClient) {
        super("Reschedule loan", "Reschedule");
        this.loanId = loanId;
        this.apiClient = apiClient;
        this.errorLabel = new Label();
        this.debtReschedulingComponent = new DebtReschedulingComponent(loanId);

        setModal(true);
        setWidth(650, Unit.PIXELS);

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(false);
        setDialogContent(layout);

        errorLabel.setVisible(false);
        layout.addComponent(errorLabel);

        layout.addComponent(debtReschedulingComponent);

        if (!debtReschedulingComponent.reschedulingAvailable()) {
            disableActionButton();
        }
    }

    @Override
    protected void executeAction() {
        Optional<String> maybeError = debtReschedulingComponent.validate();
        if (!maybeError.isPresent()) {
            RescheduleLoanRequest request = new RescheduleLoanRequest()
                .setLoanId(loanId)
                .setReschedulingPreview(debtReschedulingComponent.getReschedulingPreview());

            BackgroundOperations.callApi("Rescheduling loan", apiClient.rescheduleLoan(request), t -> {
                Notifications.trayNotification("Loan rescheduled");
                UI.getCurrent().getNavigator().navigateTo(LoanView.NAME + "/" + loanId + "?tab=schedule");
                close();
            }, Notifications::errorNotification);
        } else {
            errorLabel.setCaption(maybeError.get());
            errorLabel.setVisible(true);
        }
    }

}
