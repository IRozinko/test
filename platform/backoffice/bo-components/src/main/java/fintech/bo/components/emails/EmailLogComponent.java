package fintech.bo.components.emails;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.dto.ClientDTO;
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
import static fintech.bo.db.jooq.email.Tables.LOG;
import static fintech.bo.db.jooq.notification.Tables.NOTIFICATION_;

public class EmailLogComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    @Autowired
    private ClientComponents clientComponents;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    private BoComponentContext context;
    private EmailLogDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new EmailLogDataProvider(db, jooqClientDataService)
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
        builder.addActionColumn("Open",
            r -> {
                EmailPreviewDialog dialog = new EmailPreviewDialog(r.get(LOG.SUBJECT), r.get(LOG.BODY));
                UI.getCurrent().addWindow(dialog);
            },
            r -> r.get(CLIENT.DELETED) && !LoginService.hasPermission(context.getPermissionsForDeletedClients()));
        builder.addColumn(LOG.ID);
        builder.addLinkColumn(CLIENT.CLIENT_NUMBER, r -> ClientComponents.clientLink(r.get(NOTIFICATION_.CLIENT_ID)));
        builder.addColumn(LOG.SENDING_STATUS);
        builder.addColumn(NOTIFICATION_.CMS_KEY).setWidth(250);
        builder.addColumn(LOG.SUBJECT);
        builder.addColumn(LOG.SEND_TO);
        builder.addColumn(LOG.SEND_FROM);
        builder.addColumn(LOG.SEND_FROM_NAME);
        builder.addColumn(LOG.ATTEMPTS);
        builder.addColumn(LOG.NEXT_ATTEMPT_AT);
        builder.addAuditColumns(LOG);
        builder.sortDesc(LOG.ID);
        return builder.build(dataProvider);
    }
}
