package fintech.bo.spain.alfa.de;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
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

import static fintech.bo.db.jooq.decision_engine.DecisionEngine.DECISION_ENGINE;

public class DecisionEngineListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    @Autowired
    private ClientComponents clientComponents;

    private BoComponentContext context;
    private DecisionEngineDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new DecisionEngineDataProvider(this.db).setComponentContext(context);
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

    public Component grid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(DECISION_ENGINE.REQUEST.ID);
        if (!context.inScope(StandardScopes.SCOPE_CLIENT)) {
            builder.addLinkColumn(DECISION_ENGINE.REQUEST.CLIENT_ID, r -> ClientComponents.clientLink(r.get(DECISION_ENGINE.REQUEST.CLIENT_ID)));
        }
        builder.addColumn(DECISION_ENGINE.REQUEST.RESPONSE);
        builder.addColumn(DECISION_ENGINE.REQUEST.RATING);
        builder.addColumn(DECISION_ENGINE.REQUEST.VARIABLES_RESULT);
        builder.addColumn(DECISION_ENGINE.REQUEST.DECISION);
        builder.addColumn(DECISION_ENGINE.REQUEST.SCORE);
        builder.addColumn(DECISION_ENGINE.REQUEST.SCENARIO);
        builder.addColumn(DECISION_ENGINE.REQUEST.STATUS);
        builder.addColumn(DECISION_ENGINE.REQUEST.ERROR);
        builder.addAuditColumns(DECISION_ENGINE.REQUEST);
        builder.sortDesc(DECISION_ENGINE.REQUEST.ID);
        return builder.build(dataProvider);
    }
}
