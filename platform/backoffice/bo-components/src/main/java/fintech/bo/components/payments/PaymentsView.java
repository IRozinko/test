package fintech.bo.components.payments;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.common.Fields;
import fintech.bo.components.common.SearchField;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.db.jooq.payment.tables.records.InstitutionRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static fintech.bo.components.payments.PaymentQueries.INSTITUTION_ACCOUNT_NUMBER;
import static fintech.bo.components.payments.PaymentQueries.INSTITUTION_NAME;
import static fintech.bo.db.jooq.payment.Payment.PAYMENT;

@Slf4j
@SpringView(name = PaymentsView.NAME)
public class PaymentsView extends VerticalLayout implements View {

    public static final String NAME = "payments";

    @Autowired
    private PaymentComponents paymentComponents;

    private Grid<Record> grid;
    private PaymentsDataProvider dataProvider;

    private SearchField search;
    private ComboBox<String> status;
    private ComboBox<String> type;
    private ComboBox<InstitutionRecord> institution;
    private DateRangeField valueDate;

    @PostConstruct
    public void init() {
        dataProvider = paymentComponents.dataProvider();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Payments");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchFieldWithOptions();
        search.addValueChangeListener(event -> refresh());
        search.addFieldOptions(dataProvider.getSearchFieldsNames());

        institution = paymentComponents.institutionComboBox();
        institution.setCaption("Institution");
        institution.addValueChangeListener(event -> refresh());

        status = paymentComponents.statusComboBox();
        status.setCaption("Status");
        status.addValueChangeListener(event -> refresh());

        type = paymentComponents.typeComboBox();
        type.setCaption("Type");
        type.addValueChangeListener(event -> refresh());

        valueDate = Fields.dateRangeField("Value date");
        valueDate.addValueChangeListener(event -> refresh());

        layout.addTopComponent(search);
        layout.addTopComponent(institution);
        layout.addTopComponent(status);
        layout.addTopComponent(type);
        layout.addTopComponent(valueDate);
        layout.setRefreshAction((e) -> refresh());
    }

    private void buildGrid(GridViewLayout layout) {
        grid();
        layout.setContent(grid);
    }

    private void refresh() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.setStatusDetail(status.getValue());
        dataProvider.setType(type.getValue());
        dataProvider.setValueDateFrom(valueDate.getBeginDate());
        dataProvider.setValueDateTo(valueDate.getEndDate());
        dataProvider.setInstitutionId(institution.getValue() == null ? null : institution.getValue().getId());
        grid.getDataProvider().refreshAll();
    }

    private void grid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "payment/" + r.get(PAYMENT.PAYMENT_.ID));
        builder.addActionColumn("Distribute", r -> {
            AddTransactionDialog dialog = paymentComponents.addTransactionDialog(r.get(PAYMENT.PAYMENT_.ID));
            dialog.addCloseListener(e2 -> grid.getDataProvider().refreshAll());
            UI.getCurrent().addWindow(dialog);
        }, r -> !PaymentConstants.STATUS_MANUAL.equals(r.get(PAYMENT.PAYMENT_.STATUS_DETAIL)));
        builder.addColumn(PAYMENT.PAYMENT_.ID);
        builder.addColumn(PAYMENT.PAYMENT_.STATUS).setStyleGenerator(PaymentComponents.statusStyle());
        builder.addColumn(PAYMENT.PAYMENT_.STATUS_DETAIL).setStyleGenerator(PaymentComponents.statusStyle());
        builder.addColumn(PAYMENT.PAYMENT_.PAYMENT_TYPE).setCaption("Type");
        builder.addColumn(PAYMENT.PAYMENT_.VALUE_DATE);
        builder.addColumn(PAYMENT.PAYMENT_.AMOUNT);
        builder.addColumn(PAYMENT.PAYMENT_.PENDING_AMOUNT);
        builder.addColumn(INSTITUTION_NAME);
        builder.addColumn(INSTITUTION_ACCOUNT_NUMBER);
        builder.addColumn(PAYMENT.PAYMENT_.COUNTERPARTY_NAME);
        builder.addColumn(PAYMENT.PAYMENT_.COUNTERPARTY_ACCOUNT);
        builder.addColumn(PAYMENT.PAYMENT_.DETAILS).setSortable(false).setWidth(300);
        builder.addAuditColumns(PAYMENT.PAYMENT_);
        builder.sortDesc(PAYMENT.PAYMENT_.ID);
        grid = builder.build(dataProvider);
    }
}
