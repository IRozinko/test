package fintech.bo.spain.inglobally;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.ui.*;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.common.Fields;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import fintech.bo.spain.db.jooq.inglobaly.Tables;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

public class InglobalyListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    private BoComponentContext context;
    private InglobalyDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new InglobalyDataProvider(this.db)
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
        builder.addActionColumn("Response", this::showResponseDialog);
        builder.addColumn(Tables.RESPONSE.ID);
        if (!context.inScope(StandardScopes.SCOPE_CLIENT)) {
            builder.addLinkColumn(Tables.RESPONSE.CLIENT_ID, r -> ClientComponents.clientLink(r.get(Tables.RESPONSE.CLIENT_ID)));
        }
        builder.addColumn(Tables.RESPONSE.STATUS);
        builder.addColumn(Tables.RESPONSE.ERROR);
        builder.addColumn(Tables.RESPONSE.REQUESTED_DOCUMENT_NUMBER);
        builder.addColumn(Tables.RESPONSE.DATE_OF_BIRTH);
        builder.addColumn(Tables.RESPONSE.FIRST_NAME);
        builder.addColumn(Tables.RESPONSE.LAST_NAME);
        builder.addColumn(Tables.RESPONSE.SECOND_LAST_NAME);
        builder.addAuditColumns(Tables.RESPONSE);
        builder.sortDesc(Tables.RESPONSE.ID);
        return builder.build(dataProvider);
    }

    private void showResponseDialog(Record item) {
        Dialogs.showText("Response", item.get(Tables.RESPONSE.RESPONSE_BODY));
    }
}
