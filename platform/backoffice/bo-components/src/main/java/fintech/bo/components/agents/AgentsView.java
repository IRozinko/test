package fintech.bo.components.agents;

import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.GridHelper;
import fintech.bo.components.Refreshable;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.task.tables.records.AgentRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.task.tables.Agent.AGENT;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.CS_AGENTS_EDIT})
@SpringView(name = AgentsView.NAME)
public class AgentsView extends VerticalLayout implements View, Refreshable {
    public static final String NAME = "agents";

    @Autowired
    private AgentsComponents agentsComponents;

    private AgentDataProvider dataProvider;
    private Grid<AgentRecord> agentsGrid;
    private VerticalLayout editLayout;
    private TextField search;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Agents");
        removeAllComponents();

        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildContent(layout);
        addComponentsAndExpand(layout);
    }

    private void buildContent(GridViewLayout layout) {
        dataProvider = agentsComponents.dataProvider();
        agentsGrid = grid(dataProvider);

        editLayout = new VerticalLayout();
        editLayout.setMargin(false);

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(600, Unit.PIXELS);
        splitPanel.setFirstComponent(agentsGrid);
        splitPanel.setSecondComponent(editLayout);
        splitPanel.setSizeFull();
        layout.setContent(splitPanel);
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchField();
        search.addValueChangeListener(event -> refresh());

        layout.addActionMenuItem("Add agent",(event) -> {
            NewAgentDialog dialog = agentsComponents.newAgentDialog();
            dialog.addCloseListener((e) -> refresh());
            UI.getCurrent().addWindow(dialog);
        });
        layout.setRefreshAction(e -> refresh());
        layout.addTopComponent(search);
    }


    private void renderEditPanel(AgentRecord record) {
        Component editPermissionsForm = agentsComponents.editAgentForm(record, this::refresh);
        editLayout.removeAllComponents();
        editLayout.addComponent(editPermissionsForm);
    }

    @Override
    public void refresh() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.refreshAll();
        agentsGrid.deselectAll();
        editLayout.removeAllComponents();
    }

    public Grid<AgentRecord> grid(AgentDataProvider dataProvider) {
        Grid<AgentRecord> grid = new Grid<>();
        Grid.Column<AgentRecord, String> emailCol = grid.addColumn(AgentRecord::getEmail)
            .setCaption("Email")
            .setId(AGENT.EMAIL.getName()).setWidth(300);
        grid.addColumn(AgentRecord::getTaskTypes).setCaption("Task Types");
        grid.setSortOrder(new GridSortOrderBuilder<AgentRecord>().thenAsc(emailCol));
        grid.setDataProvider(dataProvider);
        GridHelper.addTotalCountAsCaption(grid, dataProvider);
        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::renderEditPanel));
        grid.setSizeFull();
        return grid;
    }

}
