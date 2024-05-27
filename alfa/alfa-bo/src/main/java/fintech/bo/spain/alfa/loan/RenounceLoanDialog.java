package fintech.bo.spain.alfa.loan;

import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import fintech.TimeMachine;
import fintech.bo.components.Formats;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.spain.alfa.bo.model.RenounceLoanRequest;

class RenounceLoanDialog extends ActionDialog {

    private final Long loanId;

    private final AlfaApiClient apiClient;

    private DateField renounceDate;

    RenounceLoanDialog(Long loanId, AlfaApiClient apiClient) {
        super("Renounce loan", "Renounce");
        this.loanId = loanId;
        this.apiClient = apiClient;

        setModal(true);
        setWidth(400, Unit.PIXELS);
        setDialogContent(buildForm());
    }

    private Component buildForm() {
        renounceDate = new DateField("Select Date");
        renounceDate.setDateFormat(Formats.DATE_FORMAT);
        renounceDate.setTextFieldEnabled(false);
        renounceDate.setRequiredIndicatorVisible(true);
        renounceDate.setValue(TimeMachine.today());
        renounceDate.setRangeEnd(TimeMachine.today());

        FormLayout form = new FormLayout();
        form.setWidthUndefined();
        form.addComponent(renounceDate);
        form.setSizeFull();
        return form;
    }

    @Override
    protected void executeAction() {
        if (renounceDate.getValue() == null) {
            return;
        }

        BackgroundOperations.callApi("Renouncing loan", apiClient.renounceLoan(new RenounceLoanRequest(loanId, renounceDate.getValue())), t -> {
            Notifications.trayNotification("Loan renounced");
            close();
        }, Notifications::errorNotification);
    }
}
