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
@SpringView(name = ViventorLogView.NAME)
public class ViventorLogView extends VerticalLayout implements View {

    public static final String NAME = "viventor-log";

    @Autowired
    private LoanComponents loanComponents;

    private ComboBox<Record> loansComboBox;

    @Autowired
    private DSLContext db;

    @Autowired
    private ViventorComponents viventorComponents;

    private ViventorLogDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Viventor log");

        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new ViventorLogDataProvider(db);
        layout.setContent(viventorComponents.logGrid(dataProvider));
    }


    private void buildTop(GridViewLayout layout) {
        loansComboBox = loanComponents.loansComboBox(loanComponents.dataProvider());
        loansComboBox.setWidth(250, Unit.PIXELS);
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
        dataProvider.refreshAll();
    }
}
