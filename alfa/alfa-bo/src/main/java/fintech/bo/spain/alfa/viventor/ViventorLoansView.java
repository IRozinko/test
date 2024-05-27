package fintech.bo.spain.alfa.viventor;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.loan.LoanComponents;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;

@Slf4j
@SpringView(name = ViventorLoansView.NAME)
public class ViventorLoansView extends VerticalLayout implements View {

    public static final String NAME = "viventor-loans";

    @Autowired
    private LoanComponents loanComponents;

    @Autowired
    private DSLContext db;

    @Autowired
    private ViventorComponents viventorComponents;

    private ViventorLoanDataProvider dataProvider;

    private ComboBox<Record> loansComboBox;

    private ComboBox<String> statusComboBox;


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Viventor loans");

        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new ViventorLoanDataProvider(db);
        layout.setContent(viventorComponents.loansGrid(dataProvider));
    }


    private void buildTop(GridViewLayout layout) {
        statusComboBox = statusComboBox();
        statusComboBox.setCaption("Status");
        statusComboBox.addValueChangeListener(event -> refresh());
        layout.addTopComponent(statusComboBox);

        loansComboBox = loanComponents.loansComboBox(loanComponents.dataProvider());
        loansComboBox.setWidth(250, Unit.PIXELS);
        loansComboBox.setCaption("Loan");
        loansComboBox.addValueChangeListener(event -> refresh());
        layout.addTopComponent(loansComboBox);

        layout.setRefreshAction((e) -> refresh());
    }

    private void refresh() {
        if (loansComboBox.getValue() != null) {
            dataProvider.setLoanId(loansComboBox.getValue().get(LOAN.ID));
        } else {
            dataProvider.setLoanId(null);
        }
        dataProvider.setStatus(statusComboBox.getValue());
        dataProvider.refreshAll();
    }

    private ComboBox<String> statusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Status");
        comboBox.setItems(
            ViventorConstants.STATUS_OPEN,
            ViventorConstants.STATUS_CLOSED
        );
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }
}
