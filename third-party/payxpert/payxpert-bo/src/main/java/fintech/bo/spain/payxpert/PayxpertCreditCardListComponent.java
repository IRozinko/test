package fintech.bo.spain.payxpert;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.BackofficeTheme;
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

import static fintech.bo.spain.db.jooq.payxpert.Tables.CREDIT_CARD;

public class PayxpertCreditCardListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    @Autowired
    private ClientComponents clientComponents;

    private BoComponentContext context;
    private PayxpertCreditCardDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new PayxpertCreditCardDataProvider(this.db).setComponentContext(context);
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
        builder.addColumn(CREDIT_CARD.ID);
        if (!context.inScope(StandardScopes.SCOPE_CLIENT)) {
            builder.addLinkColumn(CREDIT_CARD.CLIENT_ID, r -> ClientComponents.clientLink(r.get(CREDIT_CARD.CLIENT_ID)));
        }
        builder.addColumn(CREDIT_CARD.CARD_NUMBER);
        builder.addColumn(CREDIT_CARD.CARD_EXPIRE_YEAR);
        builder.addColumn(CREDIT_CARD.CARD_EXPIRE_MONTH);
        builder.addColumn(CREDIT_CARD.ACTIVE);
        builder.addColumn(CREDIT_CARD.RECURRING_PAYMENTS_ENABLED);
        builder.addAuditColumns(CREDIT_CARD);
        builder.sortDesc(CREDIT_CARD.ID);
        Grid<Record> grid = builder.build(dataProvider);
        grid.setStyleGenerator(creditCardGridStyle());
        return grid;
    }

    private StyleGenerator<Record> creditCardGridStyle() {
        return item -> {
            if (item.get(CREDIT_CARD.ACTIVE)) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else {
                return BackofficeTheme.TEXT_GRAY;
            }
        };
    }
}
