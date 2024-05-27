package fintech.bo.components.users;

import com.google.common.collect.Maps;
import com.vaadin.data.Binder;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.UsersApiClient;
import fintech.bo.api.model.users.RemoveUserRequest;
import fintech.bo.api.model.users.UpdateUserRequest;
import fintech.bo.components.Refreshable;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ConfirmDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.security.tables.records.UserRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static fintech.bo.db.jooq.security.Security.SECURITY;

@Component
public class UsersComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private UsersApiClient usersApiClient;

    public UserDataProvider dataProvider() {
        return new UserDataProvider(db);
    }

    public VerticalLayout editUserForm(UserRecord userRecord, Refreshable refreshable) {
        List<String> allRoles = getAllRoles();

        List<String> userRoles = db.selectFrom(SECURITY.ROLE
            .leftJoin(SECURITY.USER_ROLE)
            .on(SECURITY.ROLE.ID.eq(SECURITY.USER_ROLE.ROLE_ID)))
            .where(SECURITY.USER_ROLE.USER_ID.eq(userRecord.getId()))
            .fetch(SECURITY.ROLE.NAME);

        Map<String, Boolean> userRolesMap = Maps.newHashMap();
        allRoles.forEach(p -> userRolesMap.put(p, userRoles.contains(p)));

        Binder<Map<String, Boolean>> binder = new Binder<>();
        binder.setBean(userRolesMap);

        VerticalLayout layout = new VerticalLayout();

        userRecord.setEmail(userRecord.getEmail());
        Label emailLabel = new Label(userRecord.getEmail());
        emailLabel.addStyleName(ValoTheme.LABEL_H3);
        emailLabel.addStyleName(ValoTheme.LABEL_BOLD);
        layout.addComponent(emailLabel);

        FormLayout rolesLayout = new FormLayout();
        allRoles.forEach(role -> {
            CheckBox checkBox = new CheckBox(role);
            rolesLayout.addComponent(checkBox);
            binder.bind(checkBox,
                userRoleMap -> userRoleMap.get(role),
                (userRoleMap, value) -> userRoleMap.put(role, value));
        });
        Panel panel = new Panel("Select user roles");
        panel.setContent(rolesLayout);
        layout.addComponent(panel);

        Button save = new Button("Save");
        save.addClickListener((e) -> saveUser(userRecord, userRolesMap));
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);

        Button resetPassword = new Button("Reset password");
        resetPassword.addClickListener(e -> resetPassword(userRecord));

        Button remove = new Button("Remove");
        remove.addStyleName(ValoTheme.BUTTON_DANGER);
        remove.addClickListener(e -> removeUser(refreshable, userRecord));

        layout.addComponent(new HorizontalLayout(save, resetPassword, remove));

        return layout;
    }

    private void resetPassword(UserRecord userRecord) {
        ResetPasswordDialog dialog = new ResetPasswordDialog(userRecord.getEmail(), usersApiClient);
        UI.getCurrent().addWindow(dialog);
    }

    private void removeUser(Refreshable refreshable, UserRecord user) {
        ConfirmDialog dialog = new ConfirmDialog("Remove user?", (e) -> {
            RemoveUserRequest removeUserRequest = new RemoveUserRequest();
            removeUserRequest.setEmail(user.getEmail());
            BackgroundOperations.callApi("Removing user", usersApiClient.removeUser(removeUserRequest),
                t -> {
                    Notifications.trayNotification("User removed");
                    if (refreshable != null) {
                        refreshable.refresh();
                    }
                },
                Notifications::errorNotification);
        });
        UI.getCurrent().addWindow(dialog);
    }

    private void saveUser(UserRecord userRecord, Map<String, Boolean> userRoles) {
        UpdateUserRequest saveRoleRequest = new UpdateUserRequest();
        saveRoleRequest.setEmail(userRecord.getEmail());
        userRoles.forEach((key, value) -> {
            if (value) {
                saveRoleRequest.getRoles().add(key);
            }
        });
        BackgroundOperations.callApi("Saving User", usersApiClient.updateUser(saveRoleRequest),
            t -> Notifications.trayNotification("User saved"),
            Notifications::errorNotification);
    }

    public AddUserDialog addUserDialog() {
        List<String> allRoles = getAllRoles();
        return new AddUserDialog(usersApiClient, allRoles);
    }

    private List<String> getAllRoles() {
        return db.selectFrom(SECURITY.ROLE)
            .orderBy(SECURITY.ROLE.NAME.asc())
            .fetch(SECURITY.ROLE.NAME);
    }
}
