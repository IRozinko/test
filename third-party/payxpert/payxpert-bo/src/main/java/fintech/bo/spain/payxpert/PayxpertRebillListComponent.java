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
import fintech.bo.components.invoice.InvoiceComponents;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.payments.PaymentComponents;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static fintech.bo.spain.db.jooq.payxpert.Tables.REBILL;

public class PayxpertRebillListComponent extends VerticalLayout implements BoComponent {

    @Autowired
    private DSLContext db;

    @Autowired
    private ClientComponents clientComponents;

    private BoComponentContext context;
    private PayxpertRebillDataProvider dataProvider;

    @Override
    public void setUp(BoComponentContext context) {
        this.removeAllComponents();
        this.setMargin(false);
        this.context = context;
        this.dataProvider = new PayxpertRebillDataProvider(this.db).setComponentContext(context);
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
        builder.addColumn(REBILL.ID);
        builder.addColumn(REBILL.STATUS).setStyleGenerator(rebillStatusStyle());
        if (!context.inScope(StandardScopes.SCOPE_CLIENT)) {
            builder.addLinkColumn(REBILL.CLIENT_ID, r -> ClientComponents.clientLink(r.get(REBILL.CLIENT_ID)));
        }
        builder.addLinkColumn(REBILL.LOAN_ID, r -> LoanComponents.loanLink(r.get(REBILL.LOAN_ID)));
        builder.addLinkColumn(REBILL.INVOICE_ID, r -> InvoiceComponents.invoiceLink(r.get(REBILL.INVOICE_ID)));
        builder.addLinkColumn(REBILL.PAYMENT_ID, r -> PaymentComponents.paymentLink(r.get(REBILL.PAYMENT_ID)));
        builder.addColumn(REBILL.AMOUNT);
        builder.addColumn(REBILL.CURRENCY);
        builder.addColumn(REBILL.ERROR_CODE);
        builder.addColumn(REBILL.ERROR_MESSAGE);
        builder.addColumn(REBILL.RESPONSE_TRANSACTION_ID);
        builder.addAuditColumns(REBILL);
        builder.sortDesc(REBILL.ID);
        return builder.build(dataProvider);
    }

    private StyleGenerator<Record> rebillStatusStyle() {
        return item -> {
            String status = item.get(REBILL.STATUS);
            if ("SUCCESS".equals(status)) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else if ("ERROR".equals(status)) {
                return BackofficeTheme.TEXT_DANGER;
            } else if ("PENDING".equals(status)) {
                return BackofficeTheme.TEXT_ACTIVE;
            } else {
                return null;
            }
        };
    }
}
