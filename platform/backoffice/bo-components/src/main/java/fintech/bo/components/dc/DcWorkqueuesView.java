package fintech.bo.components.dc;

import com.google.common.eventbus.Subscribe;
import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.data.provider.Query;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.TimeMachine;
import fintech.bo.api.client.CalendarApiClient;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.PollingScheduler;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.common.Fields;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.security.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.components.dc.DcWorkqueuesView.NAME;
import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.dc.Tables.DEBT;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@SpringView(name = NAME)
public class DcWorkqueuesView extends VerticalLayout implements View {

    private static final int REQUEST_INTERVAL = 5;
    private static final int MAX_VALUE = 5000;
    private static final int DEFAULT_LIMIT = 500;
    private static final String DISPLAYED_DEBTS_CAPTION = "Displayed %s out of %s";

    public static final String NAME = "dc-work-queue";

    @Autowired
    private DSLContext db;

    @Autowired
    protected DcQueries dcQueries;

    @Autowired
    private DcComponents dcComponents;

    @Autowired
    protected DcApiClient dcApiClient;

    @Autowired
    private CmsApiClient cmsApiClient;

    @Autowired
    private CalendarApiClient calendarApiClient;

    @Autowired
    private PollingScheduler pollingScheduler;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    protected DcSettingsJson settings;

    private List<Record> debts;
    private DebtDataProvider debtsDataProvider;
    private DebtDataProvider queueDataProvider;

    private ComboBox<String> agents;
    private ComboBox<String> aging;
    private DateRangeField nextActionDate;
    private String selectedPortfolio;

    protected GridViewLayout layout;
    protected Grid<Record> queueGrid;

    private MenuBar.MenuItem notifyAndMove;
    private QueueInfoPanel queueInfoPanel;
    private ComboBox<String> statuses;
    private ComboBox<String> loanStatusDetail;
    private NumberField minDpd;
    private NumberField maxDpd;
    private NumberField limits;
    private LocalDateTime lastRefreshed = LocalDateTime.now().minusSeconds(REQUEST_INTERVAL);

    private int totalDebts;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        settings = dcQueries.getSettings();

        layout = new GridViewLayout();
        debtsDataProvider = new DebtDataProvider(db, jooqClientDataService);
        queueDataProvider = new DebtDataProvider(db, jooqClientDataService);
        setCaption("DC Work Queue");
        buildTop();
        buildGrid();
        addComponentsAndExpand(layout);
        refresh();
    }

    protected void refresh() {
        refreshQueues();
        updateQueue();
    }

    private void buildTop() {
        nextActionDate = Fields.dateRangeField("Next Action Date");
        nextActionDate.setEndDate(TimeMachine.today());
        nextActionDate.addValueChangeListener(e -> refreshQueues());

        agents = dcComponents.agentsComboBox();
        agents.setWidth(100, Unit.PERCENTAGE);
        agents.setValue(LoginService.getLoginData().getUser());
        agents.addValueChangeListener(e -> refreshQueues());

        aging = dcComponents.agingComboBox();
        aging.addValueChangeListener(event -> refresh());

        loanStatusDetail = dcComponents.loanStatusDetailComboBox();
        loanStatusDetail.addValueChangeListener(e -> updateQueue());

        layout.addGlobalFilter(nextActionDate);
        layout.addGlobalFilter(agents);
        layout.addGlobalFilter(aging);
        layout.addGlobalFilter(loanStatusDetail);

        queueInfoPanel = new QueueInfoPanel(settings.getPortfolios());
        queueInfoPanel.addQueueBtnClickListener(portfolio -> (event) -> selectQueue(portfolio));
        layout.addGlobalFilter(queueInfoPanel);

        statuses = dcComponents.statusesComboBox();
        statuses.addValueChangeListener(e -> updateQueue());
        layout.addTopComponent(statuses);

        minDpd = NumberField.builder()
            .setCaption("Min DPD")
            .setListener(e -> updateQueue())
            .build();
        layout.addTopComponent(minDpd.getComponent());

        maxDpd = NumberField.builder()
            .setCaption("Max DPD")
            .setListener(e -> updateQueue())
            .build();
        layout.addTopComponent(maxDpd.getComponent());

        limits = NumberField.builder()
            .setCaption("Limit")
            .setMaxValue(MAX_VALUE)
            .setDefaultValue(DEFAULT_LIMIT)
            .build();
        layout.addTopComponent(limits.getComponent());

        buildActions();
    }

    private void buildActions() {
        layout.setRefreshAction(e -> refresh());
        notifyAndMove = layout.addActionMenuItem("Notify and Move", (e) -> notifyAndMove());
        notifyAndMove.setEnabled(false);
    }

    private void notifyAndMove() {
        NotifyAndMoveComponent dialog = new NotifyAndMoveComponent(cmsApiClient, calendarApiClient, dcApiClient,
            settings, queueGrid.getSelectedItems());
        dialog.setSaveCallback(this::refresh);

        UI.getCurrent().addWindow(dialog);
    }

    private void buildGrid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        debts = new ArrayList<>();
        builder.addNavigationColumn("Open", r -> DcComponents.debtLinkWithBackNavigation(r.get(DEBT.ID), NAME));
        builder.addLinkColumn(DEBT.LOAN_NUMBER, r -> LoanComponents.loanLink(r.get(DEBT.LOAN_ID)));
        builder.addLinkColumn(FIELD_CLIENT_NAME, r -> ClientComponents.clientLink(r.get(DEBT.CLIENT_ID)));
        builder.addColumn(CLIENT.PHONE);
        builder.addColumn(DEBT.AGENT);
        builder.addColumn(DEBT.STATUS).setWidth(150);
        builder.addColumn(DEBT.DPD).setWidth(80);
        builder.addColumn(DEBT.TOTAL_DUE).setWidth(100);
        builder.addColumn(DEBT.LAST_ACTION);
        builder.addColumn(DEBT.LAST_ACTION_AT);
        builder.addColumn(DEBT.NEXT_ACTION);
        builder.addColumn(DEBT.NEXT_ACTION_AT);
        builder.addColumn(DEBT.PRIORITY);
        builder.addColumn(DEBT.ID);
        builder.sortAsc(DEBT.PRIORITY, DEBT.NEXT_ACTION_AT, DEBT.ID);

        queueGrid = builder.build(debts);
        queueGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        queueGrid.addSelectionListener(this::onRecordSelect);
        layout.setContent(queueGrid);
    }

    private void refreshQueues() {
        DebtFilterRequest filterRequest = getQueueFilter();
        for (DcSettingsJson.Portfolio portfolio : settings.getPortfolios()) {
            filterRequest.setPortfolio(portfolio.getName());
            queueDataProvider.setFilters(filterRequest);
            int count = queueDataProvider.size(new Query<>());
            queueInfoPanel.setCount(portfolio.getName(), count);
        }
        queueInfoPanel.refresh();
    }

    private void selectQueue(String portfolio) {
        selectedPortfolio = portfolio;
        resetGridFilters();
        updateQueue();
    }

    public void updateQueue() {
        boolean allNumberValuesValid = minDpd.isValueValid() && maxDpd.isValueValid();
        if (StringUtils.isNotBlank(selectedPortfolio) && allNumberValuesValid) {
            debtsDataProvider.setFilters(getGridFilter());
            Integer limit = Optional.ofNullable(limits.getValueOrNull()).orElse(MAX_VALUE);
            Query<Record, String> q = new Query<>(0, limit, Collections.emptyList(), null, null);
            debts = debtsDataProvider.fetch(q).collect(Collectors.toList());
            totalDebts = debtsDataProvider.size(new Query<>());
            queueGrid.setItems(debts);
            queueGrid.setCaption(format(DISPLAYED_DEBTS_CAPTION, debts.size(), totalDebts));
        }
    }

    private void onRecordSelect(SelectionEvent<Record> event) {
        notifyAndMove.setEnabled(!event.getAllSelectedItems().isEmpty());
    }

    private DebtFilterRequest getQueueFilter() {
        return new DebtFilterRequest()
            .setAgent(agents.getValue())
            .setAging(aging.getValue())
            .setLoanStatusDetail(loanStatusDetail.getValue())
            .setNextActionFrom(nextActionDate.getBeginDate())
            .setNextActionTo(nextActionDate.getEndDate());
    }

    private DebtFilterRequest getGridFilter() {
        return new DebtFilterRequest()
            .setMinDpd(minDpd.getValueOrNull())
            .setMaxDpd(maxDpd.getValueOrNull())
            .setAgent(agents.getValue())
            .setAging(aging.getValue())
            .setNextActionFrom(nextActionDate.getBeginDate())
            .setNextActionTo(nextActionDate.getEndDate())
            .setStatus(statuses.getValue())
            .setLoanStatusDetail(loanStatusDetail.getValue())
            .setPortfolio(selectedPortfolio);
    }

    private void resetGridFilters() {
        statuses.clear();
        minDpd.clear();
        maxDpd.clear();
    }

    @Override
    public void attach() {
        super.attach();
        pollingScheduler.subscribe(this);
    }

    @Override
    public void detach() {
        super.detach();
        pollingScheduler.unsubscribe(this);
    }

    @Subscribe
    public void onEvent(String tick) {
        if (lastRefreshedInSeconds() > REQUEST_INTERVAL) {
            lastRefreshed = LocalDateTime.now();
            getUI().access(this::refreshQueues);
        }
    }

    private long lastRefreshedInSeconds() {
        return SECONDS.between(lastRefreshed, LocalDateTime.now());
    }

}
