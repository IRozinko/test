package fintech.bo.components.dc;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.model.dc.DebtEditResponse;
import fintech.bo.api.model.dc.SaveDebtStatusRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.dc.tables.records.DebtRecord;
import retrofit2.Call;

import java.util.List;

public class DebtEditStateAndStatusDialog extends ActionDialog {

    private SaveDebtStatusRequest request;
    private DebtRecord debtRecord;
    private DcApiClient dcApiClient;

    public DebtEditStateAndStatusDialog(DebtRecord debtRecord, DcApiClient dcApiClient) {
        super("Debt edit status and state", "Save");
        this.debtRecord = debtRecord;
        this.dcApiClient = dcApiClient;
        this.request = new SaveDebtStatusRequest();
        this.request.setDebtId(debtRecord.getId());
        setDialogContent(content());
        setModal(true);
        setWidth(400, Unit.PIXELS);
    }
    private ComboBox<String> statusesComboBox() {
        List<String> items = DebtStatus.statusesList;
        ComboBox<String> comboBox = new ComboBox<>("Debt Status");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setPageLength(20);
        comboBox.addValueChangeListener(event -> {
            request.setStatus(event.getValue());
        });
        return comboBox;
    }
    private ComboBox<String> statesComboBox() {
        List<String> items = DebtState.statesList;
        ComboBox<String> comboBox = new ComboBox<>("Debt States");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setPageLength(20);
        comboBox.addValueChangeListener(event -> {
            request.setState(event.getValue());
        });
        return comboBox;
    }
    private ComboBox<String> subStatusesComboBox() {
        List<String> items = DebtSubStatus.subStatusesList;
        ComboBox<String> comboBox = new ComboBox<>("Debt Sub Status");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setPageLength(20);
        comboBox.addValueChangeListener(event -> {
            request.setSubStatus(event.getValue());
        });
        return comboBox;
    }
    private Component content() {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(statesComboBox());
        layout.addComponent(statusesComboBox());
        layout.addComponent(subStatusesComboBox());
        return layout;
    }

    @Override
    protected void executeAction() {
        Call<DebtEditResponse> call = dcApiClient.saveDebtStatus(request);
        BackgroundOperations.callApi("Saving debt's state and status", call, t -> {
            Notifications.trayNotification("Debt's state and status saved");
            close();
        }, Notifications::errorNotification);
    }
}
