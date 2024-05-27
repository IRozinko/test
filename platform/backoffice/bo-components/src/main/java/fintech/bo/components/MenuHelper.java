package fintech.bo.components;

import com.google.common.base.Preconditions;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import fintech.bo.components.security.SecuredView;

import java.util.List;

public class MenuHelper {

    public static void addIfAllowed(MenuBar.MenuItem menu, String caption, Class<? extends View> navigationTarget, List<String> permissions) {
        boolean isAllowed = checkPermissions(navigationTarget, permissions);
        if (isAllowed) {
            SpringView springView = navigationTarget.getAnnotation(SpringView.class);
            Preconditions.checkArgument(springView != null, "View should be annotated with SpringView");
            menu.addItem(caption, (item) -> UI.getCurrent().getNavigator().navigateTo(springView.name()));
        }
    }

    public static void addIfAllowed(MenuBar menu, String caption, Class<? extends View> navigationTarget, List<String> permissions) {
        boolean isAllowed = checkPermissions(navigationTarget, permissions);
        if (isAllowed) {
            SpringView springView = navigationTarget.getAnnotation(SpringView.class);
            Preconditions.checkArgument(springView != null, "View should be annotated with SpringView");
            menu.addItem(caption, (item) -> UI.getCurrent().getNavigator().navigateTo(springView.name()));
        }
    }

    private static boolean checkPermissions(Class<? extends View> navigationTarget, List<String> permissions) {
        SecuredView securedView = navigationTarget.getAnnotation(SecuredView.class);
        if (securedView == null) return true;
        String[] requiredPermissions = securedView.value();
        for (String requiredPermission : requiredPermissions) {
            if (permissions.contains(requiredPermission)) {
                return true;
            }
        }
        return false;
    }
}
