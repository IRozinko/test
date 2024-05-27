package fintech.bo.components.risk.checklist;

import com.google.common.base.Throwables;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JsonUtil;
import fintech.bo.api.client.ChecklistApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.risk.checklist.AddChecklistRequest;
import fintech.bo.api.model.risk.checklist.DeleteChecklistRequest;
import fintech.bo.api.model.risk.checklist.ExportChecklistRequest;
import fintech.bo.api.model.risk.checklist.ExportChecklistResponse;
import fintech.bo.api.model.risk.checklist.ImportChecklistRequest;
import fintech.bo.api.model.risk.checklist.UpdateChecklistRequest;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.dialogs.ConfirmDialog;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import fintech.bo.components.utils.CloudFileDownloader;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static fintech.bo.components.background.BackgroundOperations.callApi;
import static fintech.bo.db.jooq.checklist.Tables.CHECKLIST_ENTRY;
import static fintech.bo.db.jooq.checklist.tables.ChecklistType.CHECKLIST_TYPE;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.CHECKLIST_EDIT})
@SpringView(name = ChecklistView.NAME)
public class ChecklistView extends VerticalLayout implements View {

    public static final String NAME = "checklist";
    public static final String CAPTION = "Checklist";

    @Autowired
    private ChecklistComponents checklistComponents;

    @Autowired
    private ChecklistApiClient checklistApiClient;

    @Autowired
    private ChecklistQueries checklistQueries;

    @Autowired
    private FileApiClient fileApiClient;

    private ChecklistDataProvider dataProvider;
    private ComboBox<String> typeFilter;
    private TextField search;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption(CAPTION);

        dataProvider = checklistComponents.checklistDataProvider();
        removeAllComponents();
        GridViewLayout layout = buildTop();
        Grid<Record> grid = buildGrid(dataProvider);
        layout.setContent(grid);
        addComponentsAndExpand(layout);
        refresh();
    }

    private Grid<Record> buildGrid(ChecklistDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Edit", this::showEditDialog);
        builder.addActionColumn("Delete", this::showDeleteDialog);
        builder.addColumn(CHECKLIST_ENTRY.TYPE);
        builder.addColumn(CHECKLIST_ENTRY.VALUE1);
        builder.addColumn(CHECKLIST_ENTRY.COMMENT).setWidth(250);
        builder.addColumn(CHECKLIST_TYPE.ACTION);
        builder.addAuditColumns(CHECKLIST_ENTRY);
        builder.addColumn(CHECKLIST_ENTRY.ID);
        builder.sortDesc(CHECKLIST_ENTRY.UPDATED_AT);
        return builder.build(dataProvider);
    }

    private void showDeleteDialog(Record record) {
        ConfirmDialog dialog = new ConfirmDialog("Delete Checklist entry?", event -> {
            DeleteChecklistRequest request = new DeleteChecklistRequest();
            request.setId(record.get(CHECKLIST_ENTRY.ID));
            Call<Void> call = checklistApiClient.deleteChecklist(request);
            callApi("Deleting checklist entry", call, t -> {
                Notifications.trayNotification("Checklist entry deleted");
                refresh();
            }, Notifications::errorNotification);
        });
        dialog.addCloseListener((e) -> refresh());
        UI.getCurrent().addWindow(dialog);
    }

    private GridViewLayout buildTop() {
        GridViewLayout layout = new GridViewLayout();

        List<String> types = checklistComponents.types();
        typeFilter = new ComboBox<>("Type");
        typeFilter.setItems(types);
        typeFilter.setWidth(220, Unit.PIXELS);
        typeFilter.setTextInputAllowed(false);
        typeFilter.setEmptySelectionAllowed(false);
        if (!types.isEmpty()) {
            typeFilter.setSelectedItem(types.get(0));
        }
        typeFilter.addValueChangeListener(event -> refresh());
        layout.addTopComponent(typeFilter);

        search = layout.searchField();
        search.addValueChangeListener(event -> refresh());
        layout.addTopComponent(search);

        Button exportBtn = generateExportBtn();
        layout.addTopComponent(exportBtn);

        dataProvider.addSizeListener((size, moreRecords) -> exportBtn.setEnabled(size > 0));

        Button button = new Button("Import");
        button.addClickListener((Button.ClickListener) event -> {
            importChecklist();
        });
        layout.addTopComponent(button);

        layout.setRefreshAction((e) -> refresh());
        layout.addMenuBarItem("Add", (MenuBar.Command) selectedItem -> showAddDialog());
        return layout;
    }

    private Button generateExportBtn() {
        Button exportBtn = new Button("Export");
        CloudFileDownloader onDemandFileDownloader = new CloudFileDownloader(fileApiClient, () -> {
            ExportChecklistRequest request = new ExportChecklistRequest();
            request.setType(typeFilter.getValue());
            try {
                Response<ExportChecklistResponse> exportResponse = checklistApiClient.export(request).execute();
                if (!exportResponse.isSuccessful()) {
                    String errorBody = exportResponse.errorBody().string();
                    log.error("Checklist export API request failed: {}", errorBody);
                    JsonValue errorMessage = ((JsonObject) JsonUtil.parse(errorBody)).get("message");
                    throw new RuntimeException(errorMessage.asString());
                }
                ExportChecklistResponse response = exportResponse.body();
                return new CloudFile(response.getFileId(), response.getFileName());
            } catch (IOException e) {
                log.error("Checklist export failed", e);
                throw Throwables.propagate(e);
            }
        }, cloudFile -> {
            Notifications.trayNotification("File downloaded: " + cloudFile.getName());
        });
        onDemandFileDownloader.extend(exportBtn);
        return exportBtn;
    }

    private void importChecklist() {
        ImportChecklistDialog dialog = new ImportChecklistDialog(checklistComponents.types(), fileApiClient);
        dialog.setAction(() -> {
            ImportChecklistRequest request = dialog.getRequest();
            Call<Void> call = checklistApiClient.importChecklist(request);
            callApi("Importing checklist", call, t -> {
                Notifications.trayNotification("Checklist imported");
                dialog.close();
                refresh();
            }, Notifications::errorNotification);
        });
        dialog.addCloseListener((e) -> refresh());
        UI.getCurrent().addWindow(dialog);
    }

    private void showAddDialog() {
        AddChecklistDialog dialog = new AddChecklistDialog(typeFilter.getValue(), checklistComponents.types());
        dialog.setAction(() -> {
            AddChecklistRequest request = dialog.getRequest();

            checklistQueries.findByTypeAndValue1(request.getType(), request.getValue1()).ifPresent(record -> {
                throw new IllegalStateException(String.format("Checklist entry with type [%s] and value [%s] already exists", record.getType(), record.getValue1()));
            });

            Call<Void> call = checklistApiClient.addChecklist(request);
            callApi("Adding checklist entry", call, t -> {
                Notifications.trayNotification("Checklist entry added ");
                dialog.close();
                refresh();
            }, Notifications::errorNotification);
        });
        dialog.addCloseListener((e) -> refresh());
        UI.getCurrent().addWindow(dialog);
    }

    private void showEditDialog(Record record) {
        Long id = record.get(CHECKLIST_ENTRY.ID);
        String type = record.get(CHECKLIST_ENTRY.TYPE);
        String value = record.get(CHECKLIST_ENTRY.VALUE1);
        String comment = record.get(CHECKLIST_ENTRY.COMMENT);

        EditChecklistDialog dialog = new EditChecklistDialog(type, value, comment, checklistComponents.types());
        dialog.setAction(() -> {
            UpdateChecklistRequest request = dialog.getRequest();
            request.setId(id);

            Call<Void> call = checklistApiClient.updateChecklist(request);
            callApi("Updating checklist entry", call, t -> {
                Notifications.trayNotification("Checklist entry updated");
                dialog.close();
                refresh();
            }, Notifications::errorNotification);
        });
        dialog.addCloseListener((e) -> refresh());
        UI.getCurrent().addWindow(dialog);
    }

    private void refresh() {
        dataProvider.setTypeFilter(typeFilter.getValue());
        dataProvider.setTextFilter(search.getValue());
        dataProvider.refreshAll();
    }
}
