package fintech.bo.components.security;

import com.vaadin.navigator.View;
import com.vaadin.spring.access.ViewInstanceAccessControl;
import com.vaadin.ui.UI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViewAccessManager implements ViewInstanceAccessControl {

    @Override
    public boolean isAccessGranted(UI ui, String beanName, View view) {
        SecuredView annotation = view.getClass().getAnnotation(SecuredView.class);
        if (annotation != null) {
            String[] requiredPermissions = annotation.value();
            if (LoginService.hasPermission(requiredPermissions)) {
                return true;
            }
            log.info("User has no access to view [{}]", beanName);
            return false;
        } else {
            return true;
        }
    }
}
