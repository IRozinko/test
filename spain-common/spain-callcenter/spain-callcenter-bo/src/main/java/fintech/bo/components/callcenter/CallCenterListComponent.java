package fintech.bo.components.callcenter;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
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

import static fintech.bo.db.jooq.callcenter.Tables.CALL;
import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.presence.Tables.OUTBOUND_LOAD;
import static fintech.bo.db.jooq.presence.Tables.OUTBOUND_LOAD_RECORD;

public class CallCenterListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    @Autowired
    private ClientComponents clientComponents;

    private BoComponentContext context;
    private CallCenterLoadsDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new CallCenterLoadsDataProvider(this.db).setComponentContext(context);
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

    private Component grid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(CALL.ID.as("call_id"));
        builder.addActionColumn(OUTBOUND_LOAD_RECORD.OUTBOUND_LOAD_ID, this::showOutboundLoadInfo).setCaption("Load Id");
        builder.addLinkColumn(CLIENT.CLIENT_NUMBER, r -> ClientComponents.clientLink(r.get(CALL.CLIENT_ID)));
        builder.addColumn(CALL.STATUS.as("call_status")).setCaption("Call Status");
        builder.addColumn(OUTBOUND_LOAD_RECORD.NAME);
        builder.addColumn(OUTBOUND_LOAD_RECORD.SOURCE_ID).setCaption("Presence Source Id");
        builder.addColumn(OUTBOUND_LOAD_RECORD.STATUS).setCaption("Presence Status");
        builder.addColumn(OUTBOUND_LOAD_RECORD.QUALIFICATION_CODE);
        builder.addAuditColumns(CALL);
        return builder.build(dataProvider);
    }

    private void showOutboundLoadInfo(Record r) {
        Record outboundLoadRecord = db.selectFrom(OUTBOUND_LOAD)
            .where(OUTBOUND_LOAD.ID.eq(r.get(OUTBOUND_LOAD_RECORD.OUTBOUND_LOAD_ID)))
            .fetchOne();
        UI.getCurrent().addWindow(new OutboundLoadInfoDialog(outboundLoadRecord));
    }
}
