package fintech.bo.spain.alfa.client.phone;

import com.vaadin.ui.UI;
import fintech.bo.components.GridHelper;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.PhoneGridDataProvider;
import fintech.bo.components.jooq.JooqGrid;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.crm.tables.records.PhoneContactRecord;
import fintech.bo.spain.alfa.api.PhoneContactApiClient;
import lombok.SneakyThrows;
import retrofit2.Call;


import static fintech.bo.api.model.permissions.BackofficePermissions.CLIENT_PHONES_TAB_VIEW_AND_EDIT;
import static fintech.bo.components.jooq.JooqGridSortOrder.desc;
import static fintech.bo.components.security.LoginService.hasPermission;
import static fintech.bo.db.jooq.crm.Tables.PHONE_CONTACT;
import static fintech.bo.spain.alfa.client.phone.PhoneContactDialog.updatePhone;

public class PhoneContactGrid extends JooqGrid<PhoneContactRecord> {

    private PhoneContactApiClient apiClient;

    public PhoneContactGrid(PhoneGridDataProvider dataProvider,
                            PhoneContactApiClient apiClient) {
        this.apiClient = apiClient;

        addColumn(button("Edit", this::showEditDialog, alwaysEnabled()));
        addColumn(text(PHONE_CONTACT.COUNTRY_CODE));
        addColumn(text(PHONE_CONTACT.LOCAL_NUMBER));
        addColumn(checkBox(PHONE_CONTACT.IS_PRIMARY, this::makePhonePrimary, r -> !hasPermission(CLIENT_PHONES_TAB_VIEW_AND_EDIT) || r.getIsPrimary()));
        addColumn(checkBox(PHONE_CONTACT.ACTIVE, this::toggleActivePhone, r -> !hasPermission(CLIENT_PHONES_TAB_VIEW_AND_EDIT) || r.getIsPrimary()));
        addColumn(date(PHONE_CONTACT.ACTIVE_TILL));
        addColumn(text(PHONE_CONTACT.PHONE_TYPE));
        addColumn(text(PHONE_CONTACT.SOURCE));
        addColumn(checkBox(PHONE_CONTACT.LEGAL_CONSENT, this::toggleLegalConsent, r -> !hasPermission(CLIENT_PHONES_TAB_VIEW_AND_EDIT) || r.getIsPrimary()));
        addColumn(checkBox(PHONE_CONTACT.VERIFIED));
        addColumn(dateTime(PHONE_CONTACT.VERIFIED_AT));
        addCreatedCols(PHONE_CONTACT);

        setSortOrder(
            desc(PHONE_CONTACT.ACTIVE)
                .thenDesc(PHONE_CONTACT.IS_PRIMARY)
                .thenDesc(PHONE_CONTACT.CREATED_AT)
        );

        setDataProvider(dataProvider);
        dataProvider.addSizeListener(this::totalCountAsCaption);

        GridHelper.addTotalCountAsCaption(this, dataProvider);

        tuneGrid();
    }

    @SneakyThrows
    private void makePhonePrimary(PhoneContactRecord record, Boolean value) {
        sendRequest(apiClient.makePrimary(record.getId()));
    }

    @SneakyThrows
    private void toggleActivePhone(PhoneContactRecord record, Boolean value) {
        sendRequest(apiClient.toggleActive(record.getId()));
    }

    @SneakyThrows
    private void toggleLegalConsent(PhoneContactRecord record, Boolean value) {
        sendRequest(apiClient.toggleLegalConsent(record.getId()));
    }

    private void sendRequest(Call<?> call) {
        BackgroundOperations.callApi("Updating phone", call,
            t -> {
                Notifications.trayNotification("Phone updated");
                getDataProvider().refreshAll();
            }, Notifications::errorNotification);
    }

    private void showEditDialog(PhoneContactRecord r) {
        UI.getCurrent().addWindow(updatePhone(r, apiClient, () -> getDataProvider().refreshAll()));
    }

}
