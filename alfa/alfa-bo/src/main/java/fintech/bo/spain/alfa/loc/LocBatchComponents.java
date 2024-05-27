package fintech.bo.spain.alfa.loc;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.Refreshable;
import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.dialogs.TextAreaDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.spain.alfa.api.LocBatchApiClient;
import fintech.spain.alfa.bo.model.UploadLocClientsRequest;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static fintech.bo.components.background.BackgroundOperations.callApi;
import static fintech.bo.db.jooq.alfa.tables.LocBatch.LOC_BATCH;

@Component
public class LocBatchComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private LocBatchApiClient locBatchApiClient;


    public Grid<Record> grid(LocBatchDataProvider dataProvider, Consumer<Record> onTrigger) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();

        builder.addActionColumn("Open", record -> {
            Long batchNumber = record.get(LOC_BATCH.BATCH_NUMBER);
            LocClientsBatchDataProvider clientsDataProvider = new LocClientsBatchDataProvider(db);
            clientsDataProvider.setBatchNumber(batchNumber);

            Window dialog = new Window("Batch #" + batchNumber);
            dialog.setContent(clientsGrid(clientsDataProvider));
            dialog.setWidth(1000, Sizeable.Unit.PIXELS);
            dialog.setHeight(800, Sizeable.Unit.PIXELS);
            dialog.center();
            UI.getCurrent().addWindow(dialog);
        });

        builder.addActionColumn("Trigger", onTrigger, triggerDisabled());
        builder.addColumn(LOC_BATCH.BATCH_NUMBER);
        builder.addColumn(LocBatchDataProvider.FIELD_STARTED, "Started");
        builder.addColumn(LocBatchDataProvider.FIELD_TOTAL, "Total");
        builder.addColumn(LocBatchDataProvider.FIELD_WAITING, "Waiting");
        builder.addColumn(LocBatchDataProvider.FIELD_PENDING, "Pending");
        builder.addColumn(LocBatchDataProvider.FIELD_COMPLETED, "Completed");
        builder.addColumn(LocBatchDataProvider.FIELD_FAILED, "Failed");
        builder.addColumn(LocBatchDataProvider.FIELD_CREATED_AT, "Created At");

        return builder.build(dataProvider);
    }

    public Grid<Record> clientsGrid(LocClientsBatchDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(LOC_BATCH.ID);
        builder.addColumn(LOC_BATCH.CLIENT_ID);
        builder.addLinkColumn(LOC_BATCH.CLIENT_NUMBER, this::getLink);
        builder.addColumn(LOC_BATCH.STATUS);
        builder.addColumn(LOC_BATCH.STATUS_DETAIL);
        builder.addColumn(LOC_BATCH.CREATED_AT);
        builder.addColumn(LOC_BATCH.UPDATED_AT);
        return builder.build(dataProvider);
    }

    private String getLink(Record record) {
        if (record.get(LOC_BATCH.APPLICATION_ID) != null)
            return LoanApplicationComponents.applicationLink(record.get(LOC_BATCH.APPLICATION_ID));
        else
            return ClientComponents.clientLink(record.get(LOC_BATCH.CLIENT_ID));
    }

    public Button uploadButton(Refreshable refreshable) {
        Button upload = new Button("Upload");
        upload.addClickListener(e -> {
            Window dialog = new TextAreaDialog("Batch upload", "Client's ids separated by comma",
                "Upload", input ->
                callApi("Uploading Loc Batch clients", locBatchApiClient.upload(new UploadLocClientsRequest(input)),
                    t -> refreshable.refresh(), Notifications::errorNotification)
            );
            dialog.setWidth(1000, Sizeable.Unit.PIXELS);
            dialog.center();
            UI.getCurrent().addWindow(dialog);
        });
        return upload;
    }

    public Button triggerStartWorkflowsBtn(Refreshable refreshable) {
        Button upload = new Button("Trigger Workflows");
        upload.addClickListener(e ->
            callApi("Triggering workflows", locBatchApiClient.triggerStartWorkflows(),
                t -> refreshable.refresh(), Notifications::errorNotification)
        );
        return upload;
    }

    private Predicate<Record> triggerDisabled() {
        return record -> record.get(LocBatchDataProvider.FIELD_PENDING) == 0;
    }

}
