package fintech.bo.components.transaction;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.Formats;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.security.SecuredView;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.ACCOUNTING_VIEW})
@SpringView(name = TransactionsView.NAME)
public class TransactionsView extends VerticalLayout implements View {

    public static final String NAME = "transactions";

    @Autowired
    private TransactionComponents transactionComponents;

    @Autowired
    private LoanComponents loanComponents;

    @Autowired
    private ClientComponents clientComponents;

    private Grid<Record> grid;
    private ComboBox<String> txType;
    private TransactionDataProvider dataProvider;
    private DateField valueDate;
    private ComboBox<ClientDTO> client;
    private ComboBox<Record> loan;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Transactions");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        txType = transactionComponents.transactionTypeComboBox();
        txType.setCaption("Type");
        txType.addValueChangeListener(event -> refresh());

        valueDate = new DateField("Value date");
        valueDate.setDateFormat(Formats.DATE_FORMAT);
        valueDate.setPlaceholder(Formats.DATE_FORMAT);
        valueDate.addValueChangeListener(event -> refresh());

        client = clientComponents.clientsComboBox();
        client.setCaption("Client");
        client.setWidth(200, Unit.PIXELS);
        client.addValueChangeListener(event -> refresh());

        loan = loanComponents.loansComboBox(loanComponents.dataProvider());
        loan.setCaption("Loan");
        loan.setWidth(200, Unit.PIXELS);
        loan.addValueChangeListener(event -> refresh());

        layout.addTopComponent(txType);
        layout.addTopComponent(valueDate);
        layout.addTopComponent(client);
        layout.addTopComponent(loan);
        layout.setRefreshAction((e) -> refresh());
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = transactionComponents.dataProvider();
        grid = transactionComponents.grid(dataProvider);
        layout.setContent(grid);
    }

    private void refresh() {
        dataProvider.setTransactionType(txType.getValue());
        dataProvider.setValueDate(valueDate.getValue());
        Long clientId = client.getSelectedItem().map(ClientDTO::getId).orElse(null);
        dataProvider.setClientId(clientId);
        Long loanId = loan.getSelectedItem().map(item -> item.get(LOAN.ID)).orElse(null);
        dataProvider.setLoanId(loanId);
        grid.getDataProvider().refreshAll();
    }
}
