package fintech.bo.components.affiliate;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static fintech.bo.db.jooq.affiliate.Tables.EVENT;
import static fintech.bo.db.jooq.affiliate.Tables.PARTNER;

public class AffiliateListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    @Autowired
    private ClientComponents clientComponents;

    private BoComponentContext context;
    private AffiliateEventDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new AffiliateEventDataProvider(this.db).setComponentContext(context);
        if (!context.requiresFeature(StandardFeatures.FEATURE_COMPACT_VIEW)) {
            addComponent(filter());
        }
        addComponentsAndExpand(grid());
    }

    @Override
    public void refresh() {
        this.dataProvider.refreshAll();
    }

    private Component filter() {
        HorizontalLayout layout = new HorizontalLayout();

        ComboBox<ClientDTO> client = clientComponents.clientsComboBox();
        client.setWidth(250, Unit.PIXELS);
        client.addValueChangeListener(event -> {
            context.withScope(StandardScopes.SCOPE_CLIENT, Optional.ofNullable(event.getValue()).map(ClientDTO::getId).orElse(null));
            dataProvider.setComponentContext(context);
            refresh();
        });

        Button refresh = new Button("Refresh");
        refresh.addClickListener(e -> refresh());

        layout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        layout.addComponents(client, refresh);
        return layout;
    }

    private Grid<Record> grid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(EVENT.ID);
        builder.addColumn(PARTNER.NAME);
        builder.addColumn(EVENT.EVENT_TYPE);
        if (!context.inScope(StandardScopes.SCOPE_CLIENT)) {
            builder.addLinkColumn(EVENT.CLIENT_ID, r -> ClientComponents.clientLink(r.get(EVENT.CLIENT_ID)));
        }
        builder.addColumn(EVENT.APPLICATION_ID);
        builder.addColumn(EVENT.LOAN_ID);
        builder.addColumn(EVENT.REPORT_STATUS);
        builder.addColumn(EVENT.REPORT_URL);
        builder.addColumn(EVENT.REPORTED_AT);
        builder.addColumn(EVENT.REPORT_ERROR);
        builder.addAuditColumns(EVENT);
        builder.addColumn(EVENT.LEAD_ID);
        builder.sortDesc(EVENT.ID);
        return builder.build(dataProvider);
    }
}
