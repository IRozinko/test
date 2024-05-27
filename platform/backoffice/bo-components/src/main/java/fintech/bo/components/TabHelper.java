package fintech.bo.components;

import fintech.bo.components.common.Tab;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.security.SecuredTab;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import static fintech.bo.components.utils.SpelUtils.checkExpression;

@Slf4j
public class TabHelper {

    public static void addIfAllowed(BusinessObjectLayout layout, Tab tab) {
        SecuredTab securedTab = tab.getClass().getDeclaredAnnotation(SecuredTab.class);
        if (securedTab != null && !LoginService.hasPermission(ArrayUtils.addAll(securedTab.permissions(), securedTab.value()))) {
            ArrayList<Field> allFields = new ArrayList<>(Arrays.asList(tab.getClass().getSuperclass().getDeclaredFields()));
            Boolean conditionResult;
            try {
                conditionResult = checkExpression(allFields, tab.getClass().getSuperclass(), tab, securedTab.condition());
            } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
                log.error("Error checking SecureTab annotation", e);
                return;
            }
            if (conditionResult) {
                return;
            }
        }
        layout.addTab(tab.getCaption(), tab);
    }
}
