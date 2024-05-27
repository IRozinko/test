package fintech.bo.components.dc;

import com.google.common.collect.ImmutableList;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.TimeMachine;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.model.dc.AutoAssignDebtRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import fintech.retrofit.RetrofitHelper;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fintech.bo.db.jooq.dc.Tables.DEBT;
import static fintech.bo.db.jooq.dc.tables.Agent.AGENT;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.DC_SETTINGS_EDIT})
@SpringView(name = DcAgentsView.NAME)
public class DcAgentsView extends VerticalLayout implements View {

    public static final String NAME = "dc-agents";
    public static final int PERIOD_DAYS_IN_FUTURE = 5;

    @Autowired
    private DSLContext db;

    @Autowired
    private DcComponents dcComponents;

    @Autowired
    private DcApiClient dcApiClient;

    private List<Agent> agents = new ArrayList<>();

    private List<Period> periods = new ArrayList<>();

    private ComboBox<String> portfolios;

    private ComboBox<String> statuses;

    private GridViewLayout layout;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("DC Agents");

        layout = new GridViewLayout();
        buildTop(layout);
        addComponentsAndExpand(layout);

        refresh();
    }

    private void refresh() {
        buildPeriods();
        queryAgentData();
        buildGrid();
    }

    private void queryAgentData() {
        agents.clear();

        Map<String, Agent> agentMap = db.selectFrom(AGENT).orderBy(AGENT.AGENT_).fetch().stream().map(r -> {
            Agent agent = new Agent();
            agent.setAgent(r.getAgent());
            agent.setDisabled(r.getDisabled());
            return agent;
        }).collect(Collectors.toMap(Agent::getAgent, Function.identity()));

        Agent unassigned = new Agent();
        unassigned.setAgent(DcConstants.UNASSIGNED_AGENT);
        agentMap.put(DcConstants.UNASSIGNED_AGENT, unassigned);

        agents.addAll(agentMap.values());

        for (Period period : periods) {
            SelectConditionStep<Record2<String, Long>> select = db
                .select(DSL.when(DEBT.AGENT.isNull(), DcConstants.UNASSIGNED_AGENT).otherwise(DEBT.AGENT).as("agent"), DEBT.ID)
                .from(DEBT)
                .where();
            select.and(period.getWhereCondition());
            if (portfolios.getValue() != null) {
                select.and(DEBT.PORTFOLIO.eq(portfolios.getValue()));
            }
            if (statuses.getValue() != null) {
                select.and(DEBT.STATUS.eq(statuses.getValue()));
            }
            Result<Record2<String, Long>> result = select.fetch();
            Map<String, Result<Record2<String, Long>>> map = result.intoGroups(DEBT.AGENT);
            agentMap.forEach((email, agent) -> {
                if (map.containsKey(email)) {
                    List<Long> debtIds = map.get(email).getValues(DEBT.ID);
                    Stats stats = new Stats();
                    stats.setDebtCount(debtIds.size());
                    stats.setDebtIds(debtIds);
                    agent.getStats().put(period, stats);
                } else {
                    agent.getStats().put(period, new Stats());
                }
            });
        }
    }

    private void buildPeriods() {
        periods.clear();

        LocalDate today = TimeMachine.today();

        periods.add(Period.builder().caption("Today")
            .whereCondition(DEBT.NEXT_ACTION_AT.lessThan(today.plusDays(1).atStartOfDay()))
            .build());

        for (int i = 1; i <= PERIOD_DAYS_IN_FUTURE; i++) {
            LocalDate from = today.plusDays(i);
            periods.add(Period.builder().caption(from.format(DateTimeFormatter.ofPattern("EE, MMM dd")))
                .whereCondition(DEBT.NEXT_ACTION_AT.greaterOrEqual(from.atStartOfDay()).and(DEBT.NEXT_ACTION_AT.lessThan(today.plusDays(i + 1).atStartOfDay())))
                .build());
        }

        periods.add(Period.builder().caption("Later")
            .whereCondition(DEBT.NEXT_ACTION_AT.greaterOrEqual(today.plusDays(PERIOD_DAYS_IN_FUTURE + 1).atStartOfDay()))
            .build());

        periods.add(Period.builder().caption("No action")
            .whereCondition(DEBT.NEXT_ACTION_AT.isNull())
            .build());

        periods.add(Period.builder().caption("All debts")
            .whereCondition(DSL.trueCondition())
            .build());
    }

    private void buildGrid() {
        Grid<Agent> grid = new Grid<>("Total: " + agents.size());
        grid.setItems(agents);
        grid.addColumn(record -> {
            if (DcConstants.UNASSIGNED_AGENT.equals(record.getAgent())) {
                return new Label();
            } else {
                Button button = new Button("Edit");
                button.addStyleName(ValoTheme.BUTTON_SMALL);
                button.addClickListener(e -> editAgent(record));
                return button;
            }
        }, new ComponentRenderer()).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setWidth(80);
        grid.addColumn(Agent::getAgent).setCaption("Agent").setWidth(250).setStyleGenerator(agent -> {
            if (agent.isDisabled()) {
                return BackofficeTheme.TEXT_DANGER;
            }
            return null;
        });
        List<Grid.Column> periodColumns = new ArrayList<>();
        for (Period period : periods) {
            periodColumns.addAll(buildPeriodColumns(grid, period));
        }
        grid.addColumn(agent -> "").setCaption("");
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        HeaderRow groupingHeader = grid.prependHeaderRow();
        groupingHeader.join(periodColumns.toArray(new Grid.Column[0])).setText("Debts by next action date");
        layout.setContent(grid);
    }

    private List<Grid.Column> buildPeriodColumns(Grid<Agent> grid, Period period) {
        Grid.Column<Agent, ? extends Serializable> countColumn = grid.addColumn(agent -> {
            long debtCount = agent.getStats().get(period).getDebtCount();
            return debtCount == 0 ? "" : debtCount;
        }).setCaption(period.getCaption())
            .setWidth(80)
            .setStyleGenerator((StyleGenerator<Agent>) item -> {
                long count = item.getStats().get(period).getDebtCount();
                return count > 0 ? "v-align-right " + BackofficeTheme.TEXT_ACTIVE : BackofficeTheme.TEXT_GRAY;
            });

        Grid.Column<Agent, AbstractComponent> actionColumn = grid.addColumn(agent -> {
            Stats stats = agent.getStats().get(period);
            long debtCount = stats.getDebtCount();
            if (debtCount > 0) {
                MenuBar menuBar = new MenuBar();
                menuBar.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
                MenuBar.MenuItem menu = menuBar.addItem("", null);
                menu.addItem("Assign debts to other agents", e ->
                    Dialogs.confirm("Assign debts to other agents?", event -> autoAssign(agent, stats, true))
                );
                menu.addItem("Re-balance debts", e ->
                    Dialogs.confirm("Re-balance debts?", event -> autoAssign(agent, stats, false))
                );
                return menuBar;
            } else {
                return new Label();
            }
        }, new ComponentRenderer()).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setWidth(30);

        grid.getHeaderRow(0).join(countColumn, actionColumn).setText(period.getCaption());
        return ImmutableList.of(countColumn, actionColumn);
    }

    private void autoAssign(Agent agent, Stats stats, boolean excludeAgent) {
        BackgroundOperations.run("Assigning debts", feedback -> {
            int totalDebts = stats.getDebtIds().size();
            for (int i = 0; i < totalDebts; i++) {
                feedback.update("Assigning debt", (i + 1) / (float) totalDebts);
                AutoAssignDebtRequest request = new AutoAssignDebtRequest();
                request.setDebtId(stats.getDebtIds().get(i));
                if (excludeAgent) {
                    request.setExcludeAgent(agent.getAgent());
                }
                Call<Void> call = dcApiClient.autoAssignDebt(request);
                RetrofitHelper.syncCall(call);
            }
            return totalDebts;
        }, t -> {
            Notifications.trayNotification("Completed");
            refresh();
        }, e -> {
            Notifications.errorNotification(e);
            refresh();
        });
    }

    private void editAgent(Agent agent) {
        EditAgentDialog dialog = dcComponents.editAgent(agent.getAgent());
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void addAgent() {
        EditAgentDialog dialog = dcComponents.addAgent();
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void buildTop(GridViewLayout layout) {
        layout.setRefreshAction(e -> refresh());
        layout.addActionMenuItem("Add agent", e -> addAgent());

        portfolios = dcComponents.portfoliosComboBox();
        portfolios.addValueChangeListener(e -> refresh());
        layout.addTopComponent(portfolios);

        statuses = dcComponents.statusesComboBox();
        statuses.addValueChangeListener(e -> refresh());
        layout.addTopComponent(statuses);
    }

    @Data
    @EqualsAndHashCode(of = "agent")
    public static class Agent {
        private String agent;
        private boolean disabled;
        private Map<Period, Stats> stats = new HashMap<>();
    }

    @Data
    public static class Stats {
        private long debtCount;
        private boolean absent;
        private List<Long> debtIds;
    }

    @Builder
    @Value
    @Getter
    @EqualsAndHashCode(of = "caption")
    public static class Period {
        private String caption;
        private Condition whereCondition;
    }
}
