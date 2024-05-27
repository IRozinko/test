package fintech.bo.components;

import com.vaadin.server.DefaultErrorHandler;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


@Slf4j
public class CustomErrorHandler extends DefaultErrorHandler {

    @Override
    public void error(com.vaadin.server.ErrorEvent event) {
        log.error("Unhandled error", event.getThrowable());

        Throwable rootCause = Optional.of(event.getThrowable())
            .map(ExceptionUtils::getRootCause)
            .filter(Objects::nonNull)
            .orElse(event.getThrowable());

        String details = preformatDetails(ExceptionUtils.getStackTrace(rootCause), LoginService.isLoggedIn() ? LoginService.getLoginData().getUser() : "NO USER", LoginService.getUserPermissions().toString());

        Notifications.errorNotification(ExceptionUtils.getMessage(rootCause), details);
    }

    private String preformatDetails(String stackTrace, String user, String userPermissions) {
        return
            "Time: " + LocalDateTime.now().toString() + "\n" +
                "User: " + user + "\n" +
                "Permissions: " + userPermissions + "\n\n" +
                stackTrace;

    }
}
