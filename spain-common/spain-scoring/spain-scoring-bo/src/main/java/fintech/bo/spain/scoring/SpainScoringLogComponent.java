package fintech.bo.spain.scoring;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.common.Fields;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.spain.db.jooq.scoring.tables.Log.LOG;

public class SpainScoringLogComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    private BoComponentContext context;
    private SpainScoringLogDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new SpainScoringLogDataProvider(this.db)
            .setComponentContext(context);
        if (!context.requiresFeature(StandardFeatures.FEATURE_COMPACT_VIEW)) {
            addComponent(filter());
        }
        addComponentsAndExpand(grid());
    }

    private Component filter() {
        HorizontalLayout layout = new HorizontalLayout();
        ComboBox<String> status = new ComboBox<>("Status");
        status.setTextInputAllowed(false);
        status.setItems("OK", "ERROR");
        status.addValueChangeListener(event -> {
            dataProvider.setStatus(event.getValue());
            refresh();
        });
        DateRangeField created = Fields.dateRangeField("Created");
        created.getBeginDateField().addValueChangeListener(event -> {
            dataProvider.setCreatedFrom(created.getBeginDate());
            refresh();
        });
        created.getEndDateField().addValueChangeListener(event -> {
            dataProvider.setCreatedTo(created.getEndDate());
            refresh();
        });
        Button refresh = new Button("Refresh");
        refresh.addClickListener(e -> refresh());

        layout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        layout.addComponents(status, created, refresh);
        return layout;
    }

    @Override
    public void refresh() {
        this.dataProvider.refreshAll();
    }

    private Grid<Record> grid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Request", this::showRequestDialog,
            r -> r.get(CLIENT.DELETED) && !LoginService.hasPermission(context.getPermissionsForDeletedClients()));
        builder.addActionColumn("Response", this::showResponseDialog,
            r -> r.get(CLIENT.DELETED) && !LoginService.hasPermission(context.getPermissionsForDeletedClients()));
        builder.addColumn(LOG.ID);
        if (!context.inScope(StandardScopes.SCOPE_CLIENT)) {
            builder.addLinkColumn(CLIENT.CLIENT_NUMBER, r -> ClientComponents.clientLink(r.get(LOG.CLIENT_ID)));
        }
        builder.addLinkColumn(LOG.APPLICATION_ID, r -> LoanApplicationComponents.applicationLink(r.get(LOG.APPLICATION_ID)));
        builder.addColumn(LOG.TYPE);
        builder.addColumn(LOG.STATUS);
        builder.addColumn(LOG.SCORE);
        builder.addColumn(LOG.ERROR);
        builder.addAuditColumns(LOG);
        builder.sortDesc(LOG.ID);
        return builder.build(dataProvider);
    }

    private void showRequestDialog(Record item) {
        Dialogs.showText("Request", item.get(LOG.REQUEST_ATTRIBUTES));
    }

    private void showResponseDialog(Record item) {
        Dialogs.showText("Response", item.get(LOG.RESPONSE_BODY));
    }
}
