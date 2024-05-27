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

import static fintech.bo.spain.db.jooq.payxpert.Tables.PAYMENT_REQUEST;

public class PayxpertPaymentRequestListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    @Autowired
    private ClientComponents clientComponents;

    private BoComponentContext context;
    private PayxpertPaymentRequestDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new PayxpertPaymentRequestDataProvider(this.db).setComponentContext(context);
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
        builder.addColumn(PAYMENT_REQUEST.ID);
        if (!context.inScope(StandardScopes.SCOPE_CLIENT)) {
            builder.addLinkColumn(PAYMENT_REQUEST.CLIENT_ID, r -> ClientComponents.clientLink(r.get(PAYMENT_REQUEST.CLIENT_ID)));
        }
        builder.addColumn(PAYMENT_REQUEST.STATUS).setStyleGenerator(paymentRequestStatusStyle());
        builder.addColumn(PAYMENT_REQUEST.STATUS_DETAIL);
        builder.addColumn(PAYMENT_REQUEST.PAYMENT_TYPE);
        builder.addColumn(PAYMENT_REQUEST.OPERATION);
        builder.addColumn(PAYMENT_REQUEST.AMOUNT);
        builder.addColumn(PAYMENT_REQUEST.CURRENCY);
        builder.addColumn(PAYMENT_REQUEST.CALLBACK_RECEIVED_AT);
        builder.addColumn(PAYMENT_REQUEST.CALLBACK_TRANSACTION_ID);
        builder.addColumn(PAYMENT_REQUEST.CARD_NUMBER);
        builder.addColumn(PAYMENT_REQUEST.ERROR_CODE);
        builder.addColumn(PAYMENT_REQUEST.ERROR_MESSAGE);
        builder.addColumn(PAYMENT_REQUEST.ORDER_ID);
        builder.addColumn(PAYMENT_REQUEST.MERCHANT_TOKEN);
        builder.addColumn(PAYMENT_REQUEST.CTRL_CALLBACK_URL);
        builder.addColumn(PAYMENT_REQUEST.CTRL_REDIRECT_URL);
        builder.addAuditColumns(PAYMENT_REQUEST);
        builder.sortDesc(PAYMENT_REQUEST.ID);
        return builder.build(dataProvider);
    }

    private StyleGenerator<Record> paymentRequestStatusStyle() {
        return item -> {
            String status = item.get(PAYMENT_REQUEST.STATUS);
            if ("SUCCESS".equals(status)) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else if ("ERROR".equals(status)) {
                return BackofficeTheme.TEXT_DANGER;
            } else if ("PENDING".equals(status)) {
                return BackofficeTheme.TEXT_ACTIVE;
            } else if ("EXPIRED".equals(status)) {
                return BackofficeTheme.TEXT_GRAY;
            } else {
                return null;
            }
        };
    }
}
