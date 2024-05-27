package fintech.bo.components.dc;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.common.SearchField;
import fintech.bo.components.layouts.GridViewLayout;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static fintech.bo.db.jooq.dc.Tables.DEBT;

@Slf4j
public abstract class AbstractDebtsView extends VerticalLayout implements View {

    public static final String NAME = "debts";

    @Autowired
    private DSLContext db;

    @Autowired
    private DcComponents dcComponents;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    private DebtDataProvider dataProvider;
    private Grid<Record> grid;
    private SearchField search;
    private ComboBox<String> portfolios;
    private ComboBox<String> aging;
    private ComboBox<String> statuses;
    private ComboBox<String> managingCompanies;
    private ComboBox<String> owningCompanies;
    private ComboBox<String> subStatuses;

    @PostConstruct
    private void init() {
        dataProvider = new DebtDataProvider(db, jooqClientDataService);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Debts");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        addColumns(builder);
        builder.sortAsc(DEBT.PRIORITY, DEBT.NEXT_ACTION_AT, DEBT.ID);
        this.grid = builder.build(dataProvider);
        layout.setContent(grid);
    }

    protected abstract void addColumns(JooqGridBuilder<Record> builder);

    protected void refresh() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.setFilters(getFilters());
        this.dataProvider.refreshAll();
    }

    protected DebtFilterRequest getFilters() {
        DebtFilterRequest filter = new DebtFilterRequest()
            .setPortfolio(portfolios.getValue())
            .setAging(aging.getValue())
            .setStatus(statuses.getValue())
            .setSubStatus(subStatuses.getValue());

        filter.setManagingCompany(managingCompanies.getValue());
        filter.setOwningCompany(owningCompanies.getValue());

        return filter;
    }

    protected void buildTop(GridViewLayout layout) {
        search = layout.searchFieldWithOptions();
        search.addValueChangeListener(event -> refresh());
        search.addFieldOptions(dataProvider.getSearchFieldsNames());

        layout.addTopComponent(search);

        portfolios = dcComponents.portfoliosComboBox();
        portfolios.addValueChangeListener(event -> refresh());
        layout.addTopComponent(portfolios);

        aging = dcComponents.agingComboBox();
        aging.addValueChangeListener(event -> refresh());
        layout.addTopComponent(aging);

        statuses = dcComponents.statusesComboBox();
        statuses.addValueChangeListener(event -> refresh());
        layout.addTopComponent(statuses);

        subStatuses = dcComponents.subStatusesComboBox();
        subStatuses.addValueChangeListener(event -> refresh());
        layout.addTopComponent(subStatuses);

        managingCompanies = dcComponents.managingCompaniesComboBox();
        managingCompanies.addValueChangeListener(event -> refresh());
        layout.addTopComponent(managingCompanies);

        owningCompanies = dcComponents.owningCompaniesComboBox();
        owningCompanies.addValueChangeListener(event -> refresh());
        layout.addTopComponent(owningCompanies);

        layout.setRefreshAction((e) -> refresh());
    }

    public DebtDataProvider getDataProvider() {
        return dataProvider;
    }
}
