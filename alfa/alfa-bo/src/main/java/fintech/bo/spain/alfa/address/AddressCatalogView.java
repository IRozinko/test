package fintech.bo.spain.alfa.address;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JsonUtil;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.api.model.address.AddAddressCatalogEntryRequest;
import fintech.bo.api.model.address.AddressCatalogEntry;
import fintech.bo.api.model.address.DeleteAddressCatalogEntryRequest;
import fintech.bo.api.model.address.EditAddressCatalogEntryRequest;
import fintech.bo.api.model.address.ExportAddressCatalogResponse;
import fintech.bo.api.model.address.ImportAddressCatalogRequest;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.utils.CloudFileDownloader;
import fintech.bo.spain.alfa.api.AddressApiClient;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.AddressRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static fintech.bo.components.background.BackgroundOperations.callApi;
import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.ADDRESS;

@Slf4j
@SpringView(name = AddressCatalogView.NAME)
public class AddressCatalogView extends VerticalLayout implements View {

    public static final String NAME = "address-catalog";

    @Autowired
    private DSLContext db;

    @Autowired
    private AddressApiClient addressApiClient;

    @Autowired
    private FileApiClient fileApiClient;

    private TextField search;
    private AddressCatalogDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Address Catalog");

        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new AddressCatalogDataProvider(db);
        JooqGridBuilder<AddressRecord> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Edit", this::edit);
        builder.addColumn(ADDRESS.POSTAL_CODE);
        builder.addColumn(ADDRESS.CITY).setWidth(400);
        builder.addColumn(ADDRESS.PROVINCE);
        builder.addColumn(ADDRESS.STATE);
        builder.sortDesc(ADDRESS.POSTAL_CODE);
        builder.addActionColumn("Delete", this::delete);
        layout.setContent(builder.build(dataProvider));
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchField();
        search.addValueChangeListener(event -> refresh());
        layout.addTopComponent(search);
        layout.addActionMenuItem("Add", event -> add());
        layout.addTopComponent(exportButton());
        layout.addTopComponent(importButton());
        layout.setRefreshAction((e) -> refresh());
    }

    private void add() {
        AddressCatalogEntryDialog dialog = new AddressCatalogEntryDialog("Add Address Catalog Entry", new AddressCatalogEntry());
        dialog.setAction(() -> {
            AddAddressCatalogEntryRequest request = new AddAddressCatalogEntryRequest();
            request.setEntry(dialog.getEntry());

            Call<Void> call = addressApiClient.addAddress(request);
            callApi("Adding address catalog entry", call, t -> {
                Notifications.trayNotification("Address catalog entry added");
                dialog.close();
                refresh();
            }, Notifications::errorNotification);
        });
        dialog.addCloseListener((e) -> refresh());
        UI.getCurrent().addWindow(dialog);
    }

    private Button exportButton() {
        Button exportButton = new Button("Export");
        CloudFileDownloader onDemandFileDownloader = new CloudFileDownloader(
            fileApiClient,
            () -> {
                try {
                    Response<ExportAddressCatalogResponse> response = addressApiClient.exportAddressCatalog().execute();
                    if (!response.isSuccessful()) {
                        String errorBody = response.errorBody().string();
                        log.error("Address catalog export API request failed: {}", errorBody);
                        JsonValue errorMessage = ((JsonObject) JsonUtil.parse(errorBody)).get("message");
                        throw new RuntimeException(errorMessage.asString());
                    }
                    ExportAddressCatalogResponse body = response.body();
                    return new CloudFile(body.getFileId(), body.getOriginalFileName());
                } catch (IOException e) {
                    log.error("Address catalog export failed", e);
                    throw new IllegalStateException(e);
                }
            },
            cloudFile -> Notifications.trayNotification("File downloaded: " + cloudFile.getName()));
        onDemandFileDownloader.extend(exportButton);
        return exportButton;
    }

    private Button importButton() {
        Button importButton = new Button("Import");
        importButton.addClickListener((Button.ClickListener) event -> {
            ImportAddressCatalogDialog dialog = new ImportAddressCatalogDialog(fileApiClient);
            dialog.setAction(() -> {
                ImportAddressCatalogRequest request = dialog.getRequest();

                Call<Void> call = addressApiClient.importAddressCatalog(request);

                callApi("Importing address catalog", call, t -> {
                    Notifications.trayNotification("Address catalog imported");

                    dialog.close();
                }, Notifications::errorNotification);
            });
            dialog.addCloseListener(e -> refresh());
            UI.getCurrent().addWindow(dialog);
        });
        return importButton;
    }

    private void edit(AddressRecord addressRecord) {
        AddressCatalogEntry entry = new AddressCatalogEntry();
        entry.setPostalCode(addressRecord.getPostalCode());
        entry.setCity(addressRecord.getCity());
        entry.setProvince(addressRecord.getProvince());
        entry.setState(addressRecord.getState());

        AddressCatalogEntryDialog dialog = new AddressCatalogEntryDialog("Edit Address Catalog Entry", entry);
        dialog.setAction(() -> {
            EditAddressCatalogEntryRequest request = new EditAddressCatalogEntryRequest();
            request.setId(addressRecord.getId());
            request.setEntry(dialog.getEntry());

            Call<Void> call = addressApiClient.editAddress(request);
            callApi("Editing address catalog entry", call, t -> {
                Notifications.trayNotification("Address catalog entry edited");
                dialog.close();
                refresh();
            }, Notifications::errorNotification);
        });
        dialog.addCloseListener((e) -> refresh());
        UI.getCurrent().addWindow(dialog);
    }

    private void delete(AddressRecord addressRecord) {
        Dialogs.confirm("Are you sure?", e -> {
            DeleteAddressCatalogEntryRequest request = new DeleteAddressCatalogEntryRequest(addressRecord.getId());
            Call<Void> call = addressApiClient.deleteAddress(request);
            BackgroundOperations.callApi("Deleting address catalog entry", call, v -> {
                Notifications.trayNotification("Address catalog entry deleted");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void refresh() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.refreshAll();
    }
}
