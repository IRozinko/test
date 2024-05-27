package fintech.bo.components.instantor;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fintech.JsonUtils;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.instantor.Tables.RESPONSE;

public class InstantorListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    @Autowired
    private ClientComponents clientComponents;

    private BoComponentContext context;
    private InstantorResponseDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new InstantorResponseDataProvider(this.db).setComponentContext(context);
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
        builder.addActionColumn("Response", this::showResponseDialog,
            r -> r.get(CLIENT.DELETED) && !LoginService.hasPermission(context.getPermissionsForDeletedClients()));
        builder.addColumn(RESPONSE.ID);
        if (!context.inScope(StandardScopes.SCOPE_CLIENT)) {
            builder.addLinkColumn(CLIENT.CLIENT_NUMBER, r -> ClientComponents.clientLink(r.get(RESPONSE.CLIENT_ID)));
        }
        builder.addColumn(RESPONSE.STATUS);
        builder.addColumn(RESPONSE.PROCESSING_STATUS);
        builder.addColumn(RESPONSE.ERROR);
        builder.addAuditColumns(RESPONSE);
        builder.sortDesc(RESPONSE.ID);
        return builder.build(dataProvider);
    }

    private void showResponseDialog(Record item) {
        String json = item.get(RESPONSE.PAYLOAD_JSON);
        if ("OK".equals(item.get(RESPONSE.STATUS)) && JsonUtils.isJsonValid(json)) {
            json = JsonUtils.formatJson(json);
        }
        Dialogs.showText("Response", json);
    }
}
