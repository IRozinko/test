package fintech.bo.api.server;

import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.security.user.UserService;
import org.springframework.util.ReflectionUtils;

public class PermissionsMaintainer {

    private UserService userService;

    public PermissionsMaintainer(UserService userService) {
        this.userService = userService;
    }

    public void initPermissions() {
        ReflectionUtils.doWithFields(BackofficePermissions.class, field -> {
            Object val = ReflectionUtils.getField(field, null);
            if (val != null && val.toString().startsWith("ROLE_")) {
                userService.savePermission(val.toString());
            }
        });
    }
}
