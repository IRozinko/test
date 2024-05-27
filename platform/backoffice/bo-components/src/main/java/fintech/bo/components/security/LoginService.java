package fintech.bo.components.security;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import fintech.bo.api.model.LoginResponse;
import fintech.bo.api.model.permissions.BackofficePermissions;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static fintech.bo.db.jooq.security.Security.SECURITY;

@Component
public class LoginService {

    private static final ThreadLocal<LoginData> threadLoginData = new ThreadLocal<>();
    private static final String INITIAL_URI_FRAGMENT = "initialUriFragment";

    private final DSLContext db;

    @Autowired
    public LoginService(DSLContext db) {
        this.db = db;
    }

    @Getter
    public static class LoginData implements Serializable {
        private String jwtToken;
        private String user;
        private List<String> roles;
        private List<String> permissions;

        LoginData(String jwtToken, String user, List<String> roles, List<String> permissions) {
            this.jwtToken = jwtToken;
            this.user = user;
            this.roles = new ArrayList<>(roles);
            this.permissions = new ArrayList<>(permissions);
        }
    }

    public void login(LoginResponse response) {
        Validate.notNull(response, "Null login response");
        Validate.notNull(response.getUser(), "Null user");
        Validate.notNull(response.getToken(), "Null token");

        Result<Record2<String, String>> rolesPermissions = db.select(SECURITY.PERMISSION.NAME, SECURITY.ROLE.NAME)
            .from(SECURITY.USER
                .leftJoin(SECURITY.USER_ROLE).on(SECURITY.USER.ID.eq(SECURITY.USER_ROLE.USER_ID))
                .leftJoin(SECURITY.ROLE).on(SECURITY.ROLE.ID.eq(SECURITY.USER_ROLE.ROLE_ID))
                .leftJoin(SECURITY.ROLE_PERMISSION).on(SECURITY.ROLE_PERMISSION.ROLE_ID.eq(SECURITY.ROLE.ID))
                .leftJoin(SECURITY.PERMISSION).on(SECURITY.PERMISSION.ID.eq(SECURITY.ROLE_PERMISSION.PERMISSION_ID)))
            .where(SECURITY.USER.EMAIL.eq(response.getUser()))
            .fetch();

        List<String> permissions = new ArrayList<>(rolesPermissions.intoSet(SECURITY.PERMISSION.NAME));
        List<String> roles = new ArrayList<>(rolesPermissions.intoSet(SECURITY.ROLE.NAME));

        LoginData loginData = new LoginData(response.getToken(), response.getUser(), roles, permissions);
        VaadinSession.getCurrent().setAttribute(LoginData.class, loginData);
    }

    public static LoginData getLoginData() {
        if (VaadinSession.getCurrent() != null) {
            return VaadinSession.getCurrent().getAttribute(LoginData.class);
        } else {
            return threadLoginData.get();
        }
    }

    public static void setThreadLocalLoginData(LoginData loginData) {
        Validate.isTrue(VaadinSession.getCurrent() == null, "Can't set thread local login data in session thread");
        threadLoginData.set(loginData);
    }

    public static void cleanThreadLocalLoginData() {
        threadLoginData.remove();
    }

    public static void logout(UI ui) {
        threadLoginData.remove();
        ui.getSession().close();
    }

    public static boolean isLoggedIn() {
        return getLoginData() != null;
    }

    public String getInitialUriFragment() {
        return (String) VaadinSession.getCurrent().getAttribute(INITIAL_URI_FRAGMENT);
    }

    public void setInitialUriFragment(String initialUriFragment) {
        VaadinSession.getCurrent().setAttribute(INITIAL_URI_FRAGMENT, initialUriFragment);
    }

    public static List<String> getUserPermissions() {
        LoginData loginData = getLoginData();
        return loginData.getPermissions();
    }

    public static boolean hasPermission(String permission) {
        List<String> permissions = getUserPermissions();
        return permissions.contains(permission) || permissions.contains(BackofficePermissions.ADMIN);
    }

    public static boolean hasPermission(String... permissions) {
        return Stream.of(permissions)
            .anyMatch(LoginService::hasPermission);
    }

    public static boolean isInRole(String role) {
        return getLoginData().getRoles().contains(role);
    }

    public static boolean isInAnyRole(Collection<String> roles) {
        return roles.stream().anyMatch(LoginService::isInRole);
    }

}
