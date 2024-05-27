package fintech.bo.spain.alfa.dc;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.Formats;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.payments.PaymentComponents;
import fintech.bo.db.jooq.payment.tables.records.InstitutionRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.payment.Tables.INSTITUTION_ACCOUNT;
import static fintech.bo.db.jooq.payment.Tables.PAYMENT_;
import static fintech.bo.db.jooq.transaction.Tables.TRANSACTION_;

@SpringView(name = DcPaymentsView.NAME)
public class DcPaymentsView extends VerticalLayout implements View {

    public static final String NAME = "dc-payments";

    @Autowired
    private ClientComponents clientComponents;

    @Autowired
    private PaymentComponents paymentComponents;

    @Autowired
    private DSLContext db;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    private Grid<Record> grid;

    private ComboBox<ClientDTO> clientsComboBox;
    private DateField valueDate;
    private ComboBox<InstitutionRecord> institution;
    private TextField search;
    private DcPaymentsDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("DC Payments");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new DcPaymentsDataProvider(db, jooqClientDataService);
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "payment/" + r.get(PAYMENT_.ID));
        builder.addColumn(PAYMENT_.ID);
        builder.addColumn(CLIENT.FIRST_NAME);
        builder.addColumn(CLIENT.LAST_NAME);
        builder.addColumn(CLIENT.DOCUMENT_NUMBER);
        builder.addColumn(PAYMENT_.VALUE_DATE).setWidth(150);
        builder.addColumn(TRANSACTION_.CASH_IN).setWidth(100);
        builder.addColumn(PAYMENT_.DETAILS).setWidth(400);
        builder.addColumn(DcPaymentsDataProvider.INSTITUTION_NAME);
        builder.addColumn(INSTITUTION_ACCOUNT.ACCOUNT_NUMBER).setWidth(200);
        builder.addColumn(TRANSACTION_.TRANSACTION_SUB_TYPE).setWidth(250);
        builder.addAuditColumns(PAYMENT_);
        grid = builder.build(dataProvider);
        layout.setContent(grid);
    }

    private void refresh() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.setValueDate(valueDate.getValue());
        dataProvider.setInstitutionId(institution.getValue() == null ? null : institution.getValue().getId());
        dataProvider.setClientId(clientsComboBox.getValue() == null ? null : clientsComboBox.getValue().getId());
        dataProvider.refreshAll();
    }

    private void buildTop(GridViewLayout layout) {
        layout.setRefreshAction(e -> refresh());

        search = new TextField();
        search.setCaption("Payment details");
        search.setPlaceholder("Payment details");
        search.setWidth(200, Unit.PIXELS);
        search.addValueChangeListener(event -> refresh());

        clientsComboBox = clientComponents.clientsComboBox();
        clientsComboBox.setCaption("Client");
        clientsComboBox.setWidth(250, Unit.PIXELS);
        clientsComboBox.addValueChangeListener(event -> refresh());

        valueDate = new DateField("Value date");
        valueDate.setDateFormat(Formats.DATE_FORMAT);
        valueDate.setPlaceholder(Formats.DATE_FORMAT);
        valueDate.addValueChangeListener(event -> refresh());

        institution = paymentComponents.institutionComboBox();
        institution.setCaption("Institution");
        institution.addValueChangeListener(event -> refresh());

        layout.addTopComponent(clientsComboBox);
        layout.addTopComponent(search);
        layout.addTopComponent(institution);
        layout.addTopComponent(valueDate);
    }
}
