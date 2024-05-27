package fintech.bo.components.dc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.vaadin.data.provider.Query;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.ProductResolver;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.common.SearchField;
import fintech.bo.components.layouts.GridViewLayout;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.vaadin.ui.Grid.SelectionMode.MULTI;
import static fintech.bo.components.client.ClientComponents.clientLink;
import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.accounting.tables.Entry.ENTRY;
import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.dc.Tables.DEBT;
import static java.lang.String.format;

public abstract class AbstractBatchDebtActionsView extends VerticalLayout implements View {

    public static final String NAME = "batch-actions";

    private static final int MAX_VALUE = 25000;
    private static final int DEFAULT_LIMIT = 100;
    private static final String DISPLAYED_DEBTS_CAPTION = "Displayed %s out of %s";

    private int totalDebts;
    private Grid<Record> grid;
    private GridViewLayout layout;
    private NumberField minDpd;
    private NumberField maxDpd;
    private NumberField limits;
    private ComboBox<String> statuses;
    private ComboBox<String> loanStatusDetails;
    private ComboBox<String> portfolios;
    private ComboBox<String> aging;
    private ComboBox<String> managingCompanies;
    private ComboBox<String> owningCompanies;
    private Button removeSelectedButton;
    private BatchDebtDataProvider dataProvider;
    private List<Record> debts = new LinkedList<>();
    private SearchField searchField;
    private List<Record> selectedDebts = Lists.newArrayList();
    @Autowired
    private DSLContext db;

    @Autowired
    private DcComponents dcComponents;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    protected abstract void addCustomActions(GridViewLayout layout);

    protected abstract void onDebtsSelected(List<Record> selectedDebts);

    @PostConstruct
    public void init() {
        dataProvider = new BatchDebtDataProvider(db, jooqClientDataService);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Batch actions");
        layout = new GridViewLayout();
        buildTop();
        buildGrid();
        addCustomActions(layout);
        addComponentsAndExpand(layout);
        refresh();
    }

    protected void refresh() {
        updateFilterFields();
        Integer limit = Optional.ofNullable(limits.getValueOrNull()).orElse(MAX_VALUE);
        Query<Record, String> q = new Query<>(0, limit, Collections.emptyList(), null, null);
        debts = dataProvider.fetch(q).collect(Collectors.toList());
        totalDebts = dataProvider.size(new Query<>());
        grid.setItems(debts);
        grid.setCaption(format(DISPLAYED_DEBTS_CAPTION, debts.size(), totalDebts));
    }

    private void updateFilterFields() {
        dataProvider.setTextFilter(searchField.getValue());
        dataProvider.setFilters(
            new DebtFilterRequest()
            .setMinDpd(this.minDpd.getValueOrNull())
            .setMaxDpd(this.maxDpd.getValueOrNull())
            .setPortfolio(portfolios.getValue())
            .setAging(aging.getValue())
            .setStatus(statuses.getValue())
            .setLoanStatusDetail(loanStatusDetails.getValue())
            .setManagingCompany(managingCompanies.getValue())
            .setOwningCompany(owningCompanies.getValue())
        );
    }

    private void buildTop() {
        searchField = new SearchField();
        searchField.addValueChangeListener(event -> setRefreshButtonEnabled());
        searchField.addFieldOptions(dataProvider.getSearchFieldsNames());
        layout.addGlobalFilter(searchField);

        removeSelectedButton = new Button("Remove selected");
        removeSelectedButton.setEnabled(false);
        removeSelectedButton.addClickListener(e -> removeSelected(selectedDebts));
        layout.addGlobalFilter(removeSelectedButton);

        minDpd = NumberField.builder()
            .setCaption("Min DPD")
            .setListener(e -> setRefreshButtonEnabled())
            .build();
        layout.addGlobalFilter(minDpd.getComponent());

        maxDpd = NumberField.builder()
            .setCaption("Max DPD")
            .setListener(e -> setRefreshButtonEnabled())
            .build();
        layout.addGlobalFilter(maxDpd.getComponent());

        limits = NumberField.builder()
            .setCaption("Limit")
            .setMaxValue(MAX_VALUE)
            .setDefaultValue(DEFAULT_LIMIT)
            .setListener(e -> setRefreshButtonEnabled())
            .build();
        layout.addGlobalFilter(limits.getComponent());

        portfolios = dcComponents.portfoliosComboBox();
        portfolios.addValueChangeListener(event -> refresh());
        layout.addTopComponent(portfolios);

        aging = dcComponents.agingComboBox();
        aging.addValueChangeListener(event -> refresh());
        layout.addTopComponent(aging);

        statuses = dcComponents.statusesComboBox();
        statuses.addValueChangeListener(event -> refresh());
        layout.addTopComponent(statuses);

        loanStatusDetails = dcComponents.loanStatusDetailComboBox();
        loanStatusDetails.addValueChangeListener(event -> refresh());
        layout.addTopComponent(loanStatusDetails);

        managingCompanies = dcComponents.managingCompaniesComboBox();
        managingCompanies.addValueChangeListener(event -> refresh());
        layout.addTopComponent(managingCompanies);

        owningCompanies = dcComponents.owningCompaniesComboBox();
        owningCompanies.addValueChangeListener(event -> refresh());
        layout.addTopComponent(owningCompanies);

        layout.setRefreshAction((e) -> refresh());
    }

    private void buildGrid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        addColumns(builder);
        builder.sortAsc(DEBT.PRIORITY, DEBT.NEXT_ACTION_AT);
        grid = builder.build(debts);
        grid.setSelectionMode(MULTI);
        grid.addSelectionListener(e -> updateSelected(Lists.newArrayList(e.getAllSelectedItems())));
        layout.setContent(grid);
    }

    protected void addColumns(JooqGridBuilder<Record> builder) {
        builder.addActionColumn("Remove", r -> removeSelected(Lists.newArrayList(r)));
        builder.addNavigationColumn("Open", r -> DcComponents.debtLinkWithBackNavigation(r.get(DEBT.ID), NAME));
        builder.addLinkColumn(FIELD_CLIENT_NAME, record -> clientLink(record.get(ENTRY.CLIENT_ID)));
        builder.addColumn(CLIENT.DOCUMENT_NUMBER);
        builder.addColumn(CLIENT.PHONE);
        builder.addColumn(DEBT.PORTFOLIO);
        builder.addColumn(DEBT.STATUS);
        builder.addColumn(DEBT.LOAN_STATUS_DETAIL);
        builder.addColumn(DEBT.DPD);
        builder.addColumn(DEBT.MANAGING_COMPANY);
        builder.addColumn(DEBT.AGING_BUCKET);
        builder.addColumn(DEBT.PRIORITY);
        builder.addColumn(DEBT.LAST_ACTION);
        builder.addColumn(DEBT.NEXT_ACTION);
        builder.addColumn(DEBT.NEXT_ACTION_AT);
        if (ProductResolver.isPayday()) {
            builder.addColumn(DEBT.PERIOD_COUNT, "Term (days)");
        }
        builder.addColumn(DEBT.TOTAL_DUE);
        builder.addColumn(DEBT.TOTAL_OUTSTANDING);
        builder.addColumn(DEBT.TOTAL_PAID);
        builder.addColumn(DEBT.ID);
        builder.addAuditColumns(DEBT);
    }

    private void removeSelected(Collection<Record> records) {
        debts.removeAll(records);
        grid.setItems(debts);
        grid.setCaption(format(DISPLAYED_DEBTS_CAPTION, debts.size(), totalDebts));
    }

    private void updateSelected(List<Record> selectedDebts) {
        this.selectedDebts = selectedDebts;
        this.removeSelectedButton.setEnabled(!this.selectedDebts.isEmpty());
        this.onDebtsSelected(ImmutableList.copyOf(this.selectedDebts));
    }

    private void setRefreshButtonEnabled() {
        boolean allNumberValuesValid = minDpd.isValueValid() && maxDpd.isValueValid() && limits.isValueValid();
        layout.setRefreshEnabled(allNumberValuesValid);
        if (allNumberValuesValid) {
            refresh();
        }
    }
}
