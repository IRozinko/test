package fintech.bo.components.admintools;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.AdminToolsApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.admintools.ExecuteAdminActionRequest;
import fintech.bo.api.model.admintools.ListAdminActionsResponse;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeVersion;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.ProductResolver;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.admin_tools.tables.records.LogRecord;
import fintech.retrofit.RetrofitHelper;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.util.Optional;

import static fintech.bo.db.jooq.admin_tools.tables.Log.LOG;

@SecuredView({BackofficePermissions.ADMIN})
@Slf4j
@SpringView(name = AdminToolsView.NAME)
public class AdminToolsView extends VerticalLayout implements View {

    public static final String NAME = "admin-tools";

    @Autowired
    private AdminToolsApiClient apiClient;

    @Autowired
    private DSLContext db;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Admin Tools");
        refresh();
    }

    protected void refresh() {
        removeAllComponents();
        setSpacing(false);
        setMargin(false);

        BusinessObjectLayout layout = new BusinessObjectLayout();
        layout.setTitle("Admin tools");
        buildLeft(layout);
        buildTabs(layout);
        buildActions(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTabs(BusinessObjectLayout layout) {
        layout.addTab("Log", this::logTab);
    }

    private Component logTab() {
        AdminActionLogDataProvider dataProvider = new AdminActionLogDataProvider(db);

        JooqGridBuilder<LogRecord> builder = new JooqGridBuilder<>();
        builder.addColumn(LOG.ID);
        builder.addColumn(LOG.NAME);
        builder.addColumn(LOG.STATUS);
        builder.addColumn(LOG.MESSAGE);
        builder.addColumn(LOG.ERROR);
        builder.addColumn(LOG.PARAMS);
        builder.addColumn(LOG.CREATED_BY);
        builder.addColumn(LOG.CREATED_AT);
        builder.addAuditColumns(LOG);
        builder.sortDesc(LOG.ID);

        return builder.build(dataProvider);
    }

    private void buildLeft(BusinessObjectLayout layout) {
        layout.getLeft().setMargin(true);
        Call<ListAdminActionsResponse> call = apiClient.listAdminActions();
        Optional<ListAdminActionsResponse> actions = RetrofitHelper.syncCall(call);
        if (!actions.isPresent()) {
            return;
        }
        for (String action : actions.get().getActions()) {
            layout.addLeftComponent(buildActionPanel(action));
            layout.addLeftComponent(new Label());
        }
//        layout.addLeftComponent(new Label("Product: " + ProductResolver.PRODUCT_TYPE));
//        layout.addLeftComponent(new Label("Build version: " + BackofficeVersion.TIMESTAMP));
    }

    private Component buildActionPanel(String action) {
        TextArea params = new TextArea("Params");
        params.setRows(3);
        params.setWordWrap(false);
        params.setWidth(100, Unit.PERCENTAGE);

        Button button = new Button("Execute");
        button.addClickListener(e -> {
            Dialogs.confirm("Execute action '" + action + "' ?", ee -> {
                executeAction(action, params.getValue());
            });
        });

        VerticalLayout layout = new VerticalLayout();
        layout.addComponents(params, button);
        layout.setSizeFull();

        return new Panel(action, layout);
    }

    private void executeAction(String action, String params) {
        ExecuteAdminActionRequest request = new ExecuteAdminActionRequest();
        request.setName(action);
        request.setParams(params);
        Call<IdResponse> call = apiClient.executeAction(request);
        BackgroundOperations.callApi("Starting action", call, e -> {
            Notifications.trayNotification("Started");
            refresh();
        }, Notifications::errorNotification);
    }

    private void buildActions(BusinessObjectLayout layout) {
        layout.setRefreshAction(this::refresh);
    }
}
