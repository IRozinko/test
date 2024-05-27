package fintech.bo.components.users;

import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.Refreshable;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.security.tables.records.RoleRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.security.Security.SECURITY;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN})
@SpringView(name = RolesView.NAME)
public class RolesView extends VerticalLayout implements View, Refreshable {
    public static final String NAME = "roles";

    @Autowired
    private RolesComponents rolesComponents;

    private RoleDataProvider dataProvider;
    private Grid<RoleRecord> rolesGrid;
    private VerticalLayout editLayout;
    private TextField search;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Roles");
        removeAllComponents();

        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildContent(layout);
        addComponentsAndExpand(layout);
    }

    private void buildContent(GridViewLayout layout) {
        dataProvider = rolesComponents.dataProvider();
        rolesGrid = rolesGrid(dataProvider);

        editLayout = new VerticalLayout();
        editLayout.setMargin(false);

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(400, Unit.PIXELS);
        splitPanel.addComponent(rolesGrid);
        splitPanel.addComponent(editLayout);
        splitPanel.setSizeFull();

        layout.setContent(splitPanel);
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchField();
        search.addValueChangeListener(event -> refresh());

        layout.addActionMenuItem("Add role",(event) -> {
            AddRoleDialog dialog = rolesComponents.addRoleDialog();
            dialog.addCloseListener((e) -> refresh());
            UI.getCurrent().addWindow(dialog);
        });
        layout.setRefreshAction(e -> refresh());

        layout.addTopComponent(search);
    }

    private void renderEditPanel(RoleRecord record) {
        Component editPermissionsForm = rolesComponents.editPermissionsForm(record, this::refresh);
        editLayout.removeAllComponents();
        editLayout.addComponent(editPermissionsForm);
    }

    @Override
    public void refresh() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.refreshAll();
        rolesGrid.deselectAll();
        editLayout.removeAllComponents();
    }

    public Grid<RoleRecord> rolesGrid(RoleDataProvider dataProvider) {
        Grid<RoleRecord> grid = new Grid<>();
        Grid.Column<RoleRecord, String> nameCol = grid.addColumn(RoleRecord::getName)
            .setCaption("Name")
            .setId(SECURITY.ROLE.NAME.getName())
            .setExpandRatio(1);
        grid.setSortOrder(new GridSortOrderBuilder<RoleRecord>().thenAsc(nameCol));
        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::renderEditPanel));
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();
        return grid;
    }

}
