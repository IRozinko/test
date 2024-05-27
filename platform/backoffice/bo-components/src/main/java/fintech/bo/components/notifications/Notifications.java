package fintech.bo.components.notifications;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import fintech.bo.components.JsonUtils;
import fintech.bo.components.api.ApiCallException;
import fintech.bo.components.dialogs.ErrorDialog;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Objects;
import java.util.Optional;

public class Notifications {

    public static void trayNotification(String message, String description) {
        Notification notification = new Notification(
            message, description,
            Notification.Type.TRAY_NOTIFICATION);
        notification.setDelayMsec(2000);
        notification.setPosition(Position.BOTTOM_LEFT);
        notification.setStyleName("bo-tray-notification");
        notification.show(Page.getCurrent());
    }

    public static void trayNotification(String message) {
        trayNotification(message, null);
    }

    public static void errorNotification(Throwable throwable) {
        if (throwable instanceof ApiCallException) {
            String message = throwable.getMessage();
            String body = ((ApiCallException) throwable).getBody();
            if (JsonUtils.isValidJson(body)) {
                ObjectNode node = JsonUtils.readValue(body, ObjectNode.class);
                if (node != null && node.has("message")) {
                    message = node.get("message").asText();
                }
            }
            errorNotification(message, body);
        } else {
            Throwable rootCause = Optional.of(throwable)
                .map(ExceptionUtils::getRootCause)
                .filter(Objects::nonNull)
                .orElse(throwable);
            errorNotification(rootCause.getMessage(), ExceptionUtils.getStackTrace(rootCause));
        }
    }

    public static void errorNotification(String message, String details) {
        ErrorDialog dialog = new ErrorDialog(message, details);
        UI.getCurrent().addWindow(dialog);
    }

    public static void errorNotification(String message) {
        Notification.show(message, Notification.Type.ERROR_MESSAGE);
    }

}
