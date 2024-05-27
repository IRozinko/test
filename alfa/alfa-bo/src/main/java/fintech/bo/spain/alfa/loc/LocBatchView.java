package fintech.bo.spain.alfa.loc;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.Refreshable;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import fintech.bo.spain.alfa.api.LocBatchApiClient;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import static fintech.bo.components.background.BackgroundOperations.callApi;
import static fintech.bo.db.jooq.alfa.tables.LocBatch.LOC_BATCH;

@Slf4j
@SpringView(name = LocBatchView.NAME)
@SecuredView({BackofficePermissions.ADMIN})
public class LocBatchView extends VerticalLayout implements View, Refreshable {

    public static final String NAME = "loc-batch";

    @Autowired
    private LocBatchComponents components;

    @Autowired
    private DSLContext db;

    @Autowired
    private LocBatchApiClient apiClient;

    private LocBatchDataProvider dataProvider;
    private GridViewLayout layout;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        dataProvider = new LocBatchDataProvider(db);
        buildUi();
    }

    public void buildUi() {
        removeAllComponents();
        setCaption("Loc Batch");

        layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        layout.addTopComponent(components.uploadButton(this));
        layout.addTopComponent(components.triggerStartWorkflowsBtn(this));

        layout.setRefreshAction((e) -> refresh());
    }

    private void buildGrid(GridViewLayout layout) {
        Grid<Record> grid = components.grid(dataProvider, record -> onTrigger(record.get(LOC_BATCH.BATCH_NUMBER)));
        layout.setContent(grid);
    }

    private void onTrigger(Long batchNumber) {
        Call<Void> call = apiClient.trigger(batchNumber);
        callApi("Triggering batch", call, t -> {
            Notifications.trayNotification("Loc Batch Triggered");
            refresh();
        }, Notifications::errorNotification);
    }

    @Override
    public void refresh() {
        dataProvider.refreshAll();
    }
}
