package fintech.bo.components.payments.disbursement;

import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.ItemClickListener;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.common.SearchField;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.loan.LoanComponents;
import org.jooq.Record;

import java.util.Optional;

import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.payment.tables.Disbursement.DISBURSEMENT;
import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;
import static fintech.bo.db.jooq.transaction.tables.Transaction.TRANSACTION_;

public class SelectDisbursementDialog extends ActionDialog {

    private final DisbursementDataProvider provider;
    private final Grid<Record> grid;

    public SelectDisbursementDialog(DisbursementComponents disbursementComponents) {
        super("Select disbursement", "Select");

        provider = disbursementComponents.dataProvider();

        grid = grid(provider);
        grid.setHeight(500, Unit.PIXELS);
        grid.addItemClickListener((ItemClickListener<Record>) event -> {
            if (event.getMouseEventDetails().isDoubleClick() && getSelected().isPresent()) {
                executeAction();
            }
        });
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        SearchField search = new SearchField();
        search.setCaption("Search");
        search.addFieldOptions(provider.getSearchFieldsNames());
        search.addValueChangeListener(e -> {
            provider.setTextFilter(e.getValue());
            provider.refreshAll();
        });
        search.focus();

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.addComponent(search);
        layout.addComponentsAndExpand(grid);

        setWidth(900, Unit.PIXELS);
        setDialogContent(layout);
    }

    private Grid<Record> grid(DisbursementDataProvider provider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addLinkColumn(LOAN.LOAN_NUMBER, r -> LoanComponents.loanLink(r.get(TRANSACTION_.LOAN_ID)));
        builder.addColumn(DISBURSEMENT.VALUE_DATE);
        builder.addColumn(DISBURSEMENT.AMOUNT).setWidth(120);
        builder.addColumn(DISBURSEMENT.EXPORTED_AT);
        builder.addColumn(INSTITUTION.NAME);
        builder.addColumn(INSTITUTION_ACCOUNT.ACCOUNT_NUMBER);
        builder.addColumn(DISBURSEMENT.STATUS).setWidth(100);
        builder.addColumn(DISBURSEMENT.STATUS_DETAIL);
        builder.addColumn(DISBURSEMENT.ID);
        builder.addAuditColumns(DISBURSEMENT);
        builder.sortDesc(DISBURSEMENT.ID);
        return builder.build(provider);
    }

    public Optional<Record> getSelected() {
        return grid.getSelectedItems().stream().findFirst();
    }

    public DisbursementDataProvider getProvider() {
        return provider;
    }
}
