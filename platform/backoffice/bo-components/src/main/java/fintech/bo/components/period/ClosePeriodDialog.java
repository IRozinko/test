package fintech.bo.components.period;


import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import fintech.TimeMachine;
import fintech.bo.api.client.PeriodsApiClient;
import fintech.bo.api.model.periods.ClosePeriodRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.payments.PaymentQueries;

import java.time.LocalDate;
import java.util.Objects;


public class ClosePeriodDialog extends ActionDialog {

    private LocalDate periodDate;

    private final PeriodsApiClient periodsApiClient;

    private final PaymentQueries paymentQueries;

    public ClosePeriodDialog(LocalDate periodDate, PeriodsApiClient periodsApiClient, PaymentQueries paymentQueries) {
        super("Close period", "Confirm");
        this.periodDate = periodDate;
        this.periodsApiClient = periodsApiClient;
        this.paymentQueries = paymentQueries;

        setDialogContent(form(periodDate));
        setWidth(400, Unit.PIXELS);
    }

    @Override
    protected void executeAction() {
        ClosePeriodRequest request = new ClosePeriodRequest(periodDate);
        BackgroundOperations.callApi("Closing period", periodsApiClient.closePeriod(request), t -> {
            Notifications.trayNotification("Period closing in progress");
            close();
        }, Notifications::errorNotification);
    }

    private Component form(LocalDate periodDate) {
        PropertyLayout layout = new PropertyLayout();
        long openPayments = paymentQueries.countOpenPaymentsUntilDate(periodDate);
        long closedPayments = paymentQueries.countClosedPaymentsOnDate(periodDate);

        layout.add("Period date", periodDate);
        layout.add("Close date", TimeMachine.today());
        layout.add("Processed payments", closedPayments);

        if (openPayments > 0) {
            disableActionButton();
            Label openPaymentsField = new Label(Objects.toString(openPayments));
            openPaymentsField.addStyleName(BackofficeTheme.TEXT_DANGER);
            openPaymentsField.addStyleName(ValoTheme.LABEL_BOLD);
            layout.add("Unprocessed payments", openPaymentsField);
            layout.addSpacer();
            layout.addWarning("Period can't be closed while there're unprocessed payments");
        } else {
            layout.add("Unprocessed payments", openPayments);
        }

        return layout;
    }

}
