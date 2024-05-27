package fintech.bo.components.accounting;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.accounting.AccountingReportQuery;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.Formats;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.security.SecuredView;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;

@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.ACCOUNTING_VIEW})
@SpringView(name = AccountingView.NAME)
public class AccountingView extends VerticalLayout implements View {

    static final String NAME = "accounting";

    @Autowired
    private LoanComponents loanComponents;

    @Autowired
    private ClientComponents clientComponents;

    @Autowired
    private AccountingComponents accountingComponents;

    private AccountingTrialBalanceProvider accountingTrialBalanceProvider;

    private AccountingEntryDataProvider accountingEntryDataProvider;

    private DateField bookingDateFrom;
    private DateField bookingDateTo;
    private ComboBox<Record> loan;
    private ComboBox<ClientDTO> client;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Accounting");

        removeAllComponents();

        accountingTrialBalanceProvider = accountingComponents.trialBalanceDataProvider();
        accountingEntryDataProvider = accountingComponents.entryDataProvider();

        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);

        refresh();
    }

    private void buildTop(GridViewLayout layout) {
        bookingDateFrom = new DateField("Booking date from");
        bookingDateFrom.setDateFormat(Formats.DATE_FORMAT);
        bookingDateFrom.setValue(LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));
        bookingDateFrom.addValueChangeListener(event -> refresh());

        bookingDateTo = new DateField("Booking date to");
        bookingDateTo.setDateFormat(Formats.DATE_FORMAT);
        bookingDateTo.setValue(LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
        bookingDateTo.addValueChangeListener(event -> refresh());

        loan = loanComponents.loansComboBox(loanComponents.dataProvider());
        loan.setCaption("Loan");
        loan.setWidth(200, Unit.PIXELS);
        loan.addValueChangeListener(event -> refresh());

        client = clientComponents.clientsComboBox();
        client.setCaption("Client");
        client.setWidth(200, Unit.PIXELS);
        client.addValueChangeListener(event -> refresh());

        ComboBox<String> periods = new ComboBox<>("Periods");
        periods.setItems("Today", "This month", "Last month", "All time");
        periods.setTextInputAllowed(false);
        periods.addValueChangeListener(e -> {
            if ("Today".equals(e.getValue())) {
                bookingDateFrom.setValue(LocalDate.now());
                bookingDateTo.setValue(LocalDate.now());
            } else if ("This month".equals(e.getValue())) {
                bookingDateFrom.setValue(LocalDate.now().withDayOfMonth(1));
                bookingDateTo.setValue(LocalDate.now());
            } else if ("Last month".equals(e.getValue())) {
                bookingDateFrom.setValue(LocalDate.now().minusMonths(1).withDayOfMonth(1));
                bookingDateTo.setValue(LocalDate.now().withDayOfMonth(1).minusDays(1));
            } else if ("All time".equals(e.getValue())) {
                bookingDateFrom.setValue(LocalDate.now().minusYears(10));
                bookingDateTo.setValue(LocalDate.now().plusDays(1));
            }
        });
        periods.setValue("Today");

        layout.addTopComponent(periods);
        layout.addTopComponent(bookingDateFrom);
        layout.addTopComponent(bookingDateTo);
        layout.addTopComponent(loan);
        layout.addTopComponent(client);
        layout.setRefreshAction(e -> refresh());
    }

    private void buildGrid(GridViewLayout layout) {
        layout.setContent(accountingComponents.accountingTabs(accountingTrialBalanceProvider, accountingEntryDataProvider));
    }

    private void refresh() {
        accountingTrialBalanceProvider.setQuery(query());
        accountingTrialBalanceProvider.refreshAll();

        accountingEntryDataProvider.setQuery(query());
        accountingEntryDataProvider.refreshAll();
    }

    private AccountingReportQuery query() {
        AccountingReportQuery query = new AccountingReportQuery();
        query.setBookingDateFrom(bookingDateFrom.getValue());
        query.setBookingDateTo(bookingDateTo.getValue());
        query.setLoanId(loan.getSelectedItem().map(item -> item.get(LOAN.ID)).orElse(null));
        query.setClientId(client.getSelectedItem().map(ClientDTO::getId).orElse(null));
        return query;
    }
}
