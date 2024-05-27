package fintech.bo.spain.alfa.loan.discount;

import fintech.bo.api.model.IdRequest;
import fintech.bo.components.GridHelper;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.jooq.JooqGrid;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.spain.alfa.api.ExtensionDiscountApiClient;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.ExtensionDiscountRecord;
import retrofit2.Call;

import static fintech.bo.components.jooq.JooqGridSortOrder.desc;
import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.EXTENSION_DISCOUNT;

public class ExtensionDiscountGrid extends JooqGrid<ExtensionDiscountRecord> {

    private ExtensionDiscountApiClient apiClient;

    public ExtensionDiscountGrid(ExtensionDiscountGridDataProvider dataProvider,
                                 ExtensionDiscountApiClient apiClient) {
        this.apiClient = apiClient;

        addColumn(button("Deactivate", this::toggleDeActiveExtensionDiscount, alwaysEnabled()));
        addColumn(button("Delete", this::showDeleteDialog, alwaysEnabled()));
        addColumn(date(EXTENSION_DISCOUNT.EFFECTIVE_FROM));
        addColumn(date(EXTENSION_DISCOUNT.EFFECTIVE_TO));
        addColumn(checkBox(EXTENSION_DISCOUNT.ACTIVE));
        addColumn(text(EXTENSION_DISCOUNT.RATE_IN_PERCENT));
        addCreatedCols(EXTENSION_DISCOUNT);

        setSortOrder(
            desc(EXTENSION_DISCOUNT.ACTIVE)
                .thenDesc(EXTENSION_DISCOUNT.CREATED_AT)
        );

        setDataProvider(dataProvider);
        dataProvider.addSizeListener(this::totalCountAsCaption);

        GridHelper.addTotalCountAsCaption(this, dataProvider);

        tuneGrid();
    }


    private void toggleDeActiveExtensionDiscount(ExtensionDiscountRecord record) {
        sendRequest(apiClient.deactivateExtensionDiscount(new IdRequest(record.getId())));
    }


    private void sendRequest(Call<?> call) {
        BackgroundOperations.callApi("Updating extension discount", call,
            t -> {
                Notifications.trayNotification("Extension discount updated");
                getDataProvider().refreshAll();
            }, Notifications::errorNotification);
    }

    private void showDeleteDialog(ExtensionDiscountRecord r) {
        sendRequest(apiClient.deleteExtensionDiscount(new IdRequest(r.getId())));
    }

}
