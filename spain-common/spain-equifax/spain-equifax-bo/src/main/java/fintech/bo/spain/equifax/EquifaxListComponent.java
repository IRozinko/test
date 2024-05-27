package fintech.bo.spain.equifax;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.JooqGridBuilder;
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
import static fintech.bo.spain.db.jooq.equifax.tables.Equifax.EQUIFAX;

public class EquifaxListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    private BoComponentContext context;
    private EquifaxDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new EquifaxDataProvider(this.db)
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
        status.setItems("FOUND", "NOT_FOUND", "ERROR");
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
        builder.addColumn(EQUIFAX.ID);
        if (!context.inScope(StandardScopes.SCOPE_CLIENT)) {
            builder.addLinkColumn(CLIENT.CLIENT_NUMBER, r -> ClientComponents.clientLink(r.get(EQUIFAX.CLIENT_ID)));
        }
        builder.addColumn(EQUIFAX.STATUS);
        builder.addColumn(EQUIFAX.ERROR);
        builder.addColumn(EQUIFAX.DOCUMENT_NUMBER);
        builder.addColumn(EQUIFAX.NUMBER_OF_CONSUMER_CREDIT_OPERATIONS);
        builder.addColumn(EQUIFAX.NUMBER_OF_CREDIT_CARD_OPERATIONS);
        builder.addColumn(EQUIFAX.NUMBER_OF_CREDITORS);
        builder.addColumn(EQUIFAX.NUMBER_OF_DAYS_OF_WORST_SITUATION);
        builder.addColumn(EQUIFAX.NUMBER_OF_MORTGAGE_OPERATIONS);
        builder.addColumn(EQUIFAX.NUMBER_OF_PERSONAL_LOAN_OPERATIONS);
        builder.addColumn(EQUIFAX.NUMBER_OF_TELCO_OPERATIONS);
        builder.addColumn(EQUIFAX.TOTAL_NUMBER_OF_OPERATIONS);
        builder.addColumn(EQUIFAX.TOTAL_NUMBER_OF_OTHER_UNPAID);
        builder.addColumn(EQUIFAX.TOTAL_UNPAID_BALANCE);
        builder.addColumn(EQUIFAX.UNPAID_BALANCE_OF_CONSUMER_CREDIT);
        builder.addColumn(EQUIFAX.UNPAID_BALANCE_OF_CREDIT_CARD);
        builder.addColumn(EQUIFAX.UNPAID_BALANCE_OF_MORTGAGE);
        builder.addColumn(EQUIFAX.UNPAID_BALANCE_OF_OTHER);
        builder.addColumn(EQUIFAX.UNPAID_BALANCE_OF_OTHER_PRODUCTS);
        builder.addColumn(EQUIFAX.UNPAID_BALANCE_OF_PERSONAL_LOAN);
        builder.addColumn(EQUIFAX.UNPAID_BALANCE_OF_TELCO);
        builder.addColumn(EQUIFAX.UNPAID_BALANCE_OWN_ENTITY);
        builder.addColumn(EQUIFAX.WORST_SITUATION_CODE);
        builder.addColumn(EQUIFAX.WORST_UNPAID_BALANCE);
        builder.addAuditColumns(EQUIFAX);
        builder.sortDesc(EQUIFAX.ID);
        return builder.build(dataProvider);
    }

    private void showResponseDialog(Record item) {
        Dialogs.showText("Response", item.get(EQUIFAX.RESPONSE_BODY));
    }

    private void showRequestDialog(Record item) {
        Dialogs.showText("Request", item.get(EQUIFAX.REQUEST_BODY));
    }
}
