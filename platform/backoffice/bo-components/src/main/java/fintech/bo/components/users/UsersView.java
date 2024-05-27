package fintech.bo.components.users;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.Refreshable;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.security.tables.records.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

import static fintech.bo.db.jooq.security.Security.SECURITY;
import static fintech.bo.db.jooq.security.tables.Role.ROLE;
import static fintech.bo.db.jooq.security.tables.UserRole.USER_ROLE;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN})
@SpringView(name = UsersView.NAME)
public class UsersView extends VerticalLayout implements View, Refreshable {
    public static final String NAME = "users";

    @Autowired
    private UsersComponents usersComponents;

    @Autowired
    private DSLContext db;

    private UserDataProvider dataProvider;
    private Grid<UserRecord> usersGrid;
    private VerticalLayout editLayout;
    private TextField search;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Users");
        removeAllComponents();

        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildContent(layout);
        addComponentsAndExpand(layout);
    }

    private void buildContent(GridViewLayout layout) {
        dataProvider = usersComponents.dataProvider();
        usersGrid = usersGrid(dataProvider);

        editLayout = new VerticalLayout();
        editLayout.setMargin(false);

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(600, Unit.PIXELS);
        splitPanel.setFirstComponent(usersGrid);
        splitPanel.setSecondComponent(editLayout);
        splitPanel.setSizeFull();
        layout.setContent(splitPanel);
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchField();
        search.addValueChangeListener(event -> refresh());

        layout.addActionMenuItem("Add user", (event) -> {
            AddUserDialog dialog = usersComponents.addUserDialog();
            dialog.addCloseListener((e) -> refresh());
            UI.getCurrent().addWindow(dialog);
        });
        layout.setRefreshAction(e -> refresh());
        layout.addTopComponent(search);
    }

    public Grid<UserRecord> usersGrid(UserDataProvider dataProvider) {
        Grid<UserRecord> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        Grid.Column<UserRecord, String> emailCol = grid.addColumn(UserRecord::getEmail)
            .setCaption("Email")
            .setId(SECURITY.USER.EMAIL.getName())
            .setWidth(200);
        grid.addColumn((ValueProvider<UserRecord, Object>) userRecord -> db.select(ROLE.NAME)
            .from(USER_ROLE).join(ROLE).on(USER_ROLE.ROLE_ID.eq(ROLE.ID))
            .where(USER_ROLE.USER_ID.eq(userRecord.getId()))
            .fetch().stream().map(Record1::value1).collect(Collectors.joining(", ")))
            .setSortable(false).setCaption("Roles").setId("roles");
        grid.setSortOrder(new GridSortOrderBuilder<UserRecord>().thenAsc(emailCol));
        grid.setSizeFull();

        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::renderEditPanel));
        return grid;
    }


    private void renderEditPanel(UserRecord record) {
        Component editPermissionsForm = usersComponents.editUserForm(record, this::refresh);
        editLayout.removeAllComponents();
        editLayout.addComponent(editPermissionsForm);
    }

    @Override
    public void refresh() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.refreshAll();
        usersGrid.deselectAll();
        editLayout.removeAllComponents();
    }
}
