package fintech.bo.components.loan;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.common.Fields;
import fintech.bo.components.common.SearchField;
import fintech.bo.components.layouts.GridViewLayout;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
public abstract class AbstractLoansView extends VerticalLayout implements View {

    public static final String NAME = "loans";

    protected final LoanComponents loanComponents;

    protected Grid<Record> grid;
    protected SearchField search;
    protected ComboBox<String> status;
    protected LoanDataProvider dataProvider;
    protected DateRangeField issueDate;
    protected DateRangeField dueDate;
    protected DateRangeField closeDate;

    protected AbstractLoansView(LoanComponents loanComponents) {
        this.loanComponents = Objects.requireNonNull(loanComponents);
    }

    @PostConstruct
    public void init() {
        dataProvider = loanComponents.dataProvider();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Loans");
        GridViewLayout layout = new GridViewLayout();

        buildGrid(layout);
        buildTop(layout);
        addComponentsAndExpand(layout);
    }

    protected void buildTop(GridViewLayout layout) {
        search = layout.searchFieldWithOptions();
        search.addValueChangeListener(event -> refresh());
        search.addFieldOptions(dataProvider.getSearchFieldsNames());

        status = loanComponents.statusComboBox();
        status.setCaption("Status");
        status.addValueChangeListener(event -> refresh());

        issueDate = Fields.dateRangeField("Issue date");
        issueDate.addValueChangeListener(event -> refresh());

        dueDate = Fields.dateRangeField("Due date");
        dueDate.addValueChangeListener(event -> refresh());

        closeDate = Fields.dateRangeField("Close date");
        closeDate.addValueChangeListener(event -> refresh());

        layout.addTopComponent(search);
        layout.addTopComponent(status);
        layout.addTopComponent(issueDate);
        layout.addTopComponent(dueDate);
        layout.addTopComponent(closeDate);
        layout.setRefreshAction((e) -> refresh());
    }

    protected void buildGrid(GridViewLayout layout) {
        grid = loanComponents.grid(dataProvider);
        layout.setContent(grid);
    }

    protected void refresh() {
        refreshSearchValues();
        refreshDataProvider();
    }

    protected void refreshSearchValues() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.setStatusDetail(status.getValue());
        dataProvider.setIssueDateFrom(issueDate.getBeginDate());
        dataProvider.setIssueDateTo(issueDate.getEndDate());
        dataProvider.setDueDateFrom(dueDate.getBeginDate());
        dataProvider.setDueDateTo(dueDate.getEndDate());
        dataProvider.setCloseDateFrom(closeDate.getBeginDate());
        dataProvider.setCloseDateTo(closeDate.getEndDate());
    }

    protected void refreshDataProvider() {
        grid.getDataProvider().refreshAll();
    }

    public LoanDataProvider getDataProvider() {
        return dataProvider;
    }
}
