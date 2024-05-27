package fintech.bo.spain.experian;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
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
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.spain.db.jooq.experian.tables.CaisResumen.CAIS_RESUMEN;

@Slf4j
public class ExperianCaisResumenListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    private BoComponentContext context;
    private ExperianCaisResumenDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new ExperianCaisResumenDataProvider(this.db)
            .setComponentContext(context);
        if (!context.requiresFeature(StandardFeatures.FEATURE_COMPACT_VIEW)) {
            addComponent(filter());
        }
        addComponentsAndExpand(grid());
    }

    @Override
    public void refresh() {
        this.dataProvider.refreshAll();
    }

    private Component grid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Request", this::showRequestDialog,
            r -> r.get(CLIENT.DELETED) && !LoginService.hasPermission(context.getPermissionsForDeletedClients()));
        builder.addActionColumn("Response", this::showResponseDialog,
            r -> r.get(CLIENT.DELETED) && !LoginService.hasPermission(context.getPermissionsForDeletedClients()));
        builder.addColumn(CAIS_RESUMEN.ID);
        if (!context.inScope(StandardScopes.SCOPE_CLIENT)) {
            builder.addLinkColumn(CLIENT.CLIENT_NUMBER, r -> ClientComponents.clientLink(r.get(CAIS_RESUMEN.CLIENT_ID)));
        }
        builder.addColumn(CAIS_RESUMEN.STATUS);
        builder.addColumn(CAIS_RESUMEN.ERROR);
        builder.addColumn(CAIS_RESUMEN.DOCUMENT_NUMBER);
        builder.addColumn(CAIS_RESUMEN.IMPORTE_TOTAL_IMPAGADO);
        builder.addColumn(CAIS_RESUMEN.MAXIMO_IMPORTE_IMPAGADO);
        builder.addColumn(CAIS_RESUMEN.NUMERO_TOTAL_CUOTAS_IMPAGADAS);
        builder.addColumn(CAIS_RESUMEN.NUMERO_TOTAL_OPERACIONES_IMPAGADAS);
        builder.addColumn(CAIS_RESUMEN.PEOR_SITUACION_PAGO);
        builder.addColumn(CAIS_RESUMEN.PEOR_SITUACION_PAGO_HISTORICA);
        builder.addAuditColumns(CAIS_RESUMEN);
        builder.sortDesc(CAIS_RESUMEN.ID);
        return builder.build(dataProvider);
    }

    private void showResponseDialog(Record item) {
        Dialogs.showText("Response", item.get(CAIS_RESUMEN.RESPONSE_BODY));
    }

    private void showRequestDialog(Record item) {
        Dialogs.showText("Request", item.get(CAIS_RESUMEN.REQUEST_BODY));
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
}
