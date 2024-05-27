package fintech.bo.components.users;

import com.google.common.collect.Maps;
import com.vaadin.data.Binder;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.RolesApiClient;
import fintech.bo.api.model.permissions.SaveRoleRequest;
import fintech.bo.components.Refreshable;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ConfirmDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.security.tables.records.RoleRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static fintech.bo.db.jooq.security.Security.SECURITY;

@Component
public class RolesComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private RolesApiClient rolesApiClient;

    public RoleDataProvider dataProvider() {
        return new RoleDataProvider(db);
    }

    public VerticalLayout editPermissionsForm(RoleRecord roleRecord, Refreshable refreshable) {
        List<String> allPermissions = getAllPermissionNames();

        List<String> userPermissions = db.selectFrom(SECURITY.PERMISSION
            .leftJoin(SECURITY.ROLE_PERMISSION)
            .on(SECURITY.PERMISSION.ID.eq(SECURITY.ROLE_PERMISSION.PERMISSION_ID)))
            .where(SECURITY.ROLE_PERMISSION.ROLE_ID.eq(roleRecord.getId()))
            .fetch(SECURITY.PERMISSION.NAME);

        Map<String, Boolean> rolePermissions = Maps.newHashMap();
        allPermissions.forEach(p -> rolePermissions.put(p, userPermissions.contains(p)));

        Binder<Map<String, Boolean>> permissionsBinder = new Binder<>();
        permissionsBinder.setBean(rolePermissions);

        VerticalLayout layout = new VerticalLayout();

        roleRecord.setName(roleRecord.getName());
        Label roleLabel = new Label(roleRecord.getName());
        roleLabel.addStyleName(ValoTheme.LABEL_H3);
        roleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        layout.addComponent(roleLabel);


        FormLayout permissionsLayout = new FormLayout();
        allPermissions.forEach(permission -> {
            CheckBox checkBox = new CheckBox(StringUtils.remove(permission, "ROLE_"));
            permissionsLayout.addComponent(checkBox);
            permissionsBinder.bind(checkBox,
                rolePermissionsMap -> rolePermissionsMap.get(permission),
                (rolePermissionsMap, value) -> rolePermissionsMap.put(permission, value));
        });
        Panel panel = new Panel("Select permissions");
        panel.setContent(permissionsLayout);
        layout.addComponent(panel);

        Button save = new Button("Save");
        save.addClickListener((e) -> saveRole(roleRecord, rolePermissions));
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);

        Button disable = new Button("Disable");
        disable.addStyleName(ValoTheme.BUTTON_DANGER);
        disable.addClickListener(e -> deleteRole(refreshable, roleRecord));

        layout.addComponent(new HorizontalLayout(save, disable));
        return layout;
    }

    private void deleteRole(Refreshable refreshable, RoleRecord role) {
        ConfirmDialog dialog = new ConfirmDialog("Disable role?", (e) -> {
            BackgroundOperations.callApi("Disabling role", rolesApiClient.delete(role.getName()),
                t -> {
                    Notifications.trayNotification("Role disabled");
                    if (refreshable != null) {
                        refreshable.refresh();
                    }
                },
                Notifications::errorNotification);
        });
        UI.getCurrent().addWindow(dialog);
    }

    private void saveRole(RoleRecord roleRecord, Map<String, Boolean> rolePermissions) {
        SaveRoleRequest saveRoleRequest = new SaveRoleRequest();
        saveRoleRequest.setName(roleRecord.getName());
        rolePermissions.forEach((key, value) -> {
            if (value) {
                saveRoleRequest.getPermissions().add(key);
            }
        });
        BackgroundOperations.callApi("Saving Role", rolesApiClient.update(saveRoleRequest),
            t -> Notifications.trayNotification("Role saved"),
            Notifications::errorNotification);
    }


    public AddRoleDialog addRoleDialog() {
        List<String> restrictedNames = db.selectFrom(SECURITY.ROLE).fetch(SECURITY.ROLE.NAME);
        return new AddRoleDialog(rolesApiClient, restrictedNames);
    }

    private List<String> getAllPermissionNames() {
        return db.selectFrom(SECURITY.PERMISSION)
            .orderBy(SECURITY.PERMISSION.NAME.asc())
            .fetch(SECURITY.PERMISSION.NAME);
    }
}
