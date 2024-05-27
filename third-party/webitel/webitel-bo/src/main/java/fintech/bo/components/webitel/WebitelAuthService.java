package fintech.bo.components.webitel;

import com.vaadin.server.VaadinSession;
import fintech.TimeMachine;
import fintech.webitel.model.WebitelAuthToken;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class WebitelAuthService {

    @Getter
    public static class WebitelData implements Serializable {
        private String key;
        private String token;
        private Long expires;
        private String username;

        public WebitelData(String key, String token, Long expires, String username) {
            this.key = key;
            this.token = token;
            this.expires = expires;
            this.username = username;
        }
    }

    public static void saveLogin(WebitelAuthToken authToken) {
        Validate.notNull(authToken, "Null login response");
        Validate.notNull(authToken.getUsername(), "Null username");
        Validate.notNull(authToken.getToken(), "Null authToken");

        WebitelData data = new WebitelData(
            authToken.getKey(),
            authToken.getToken(),
            authToken.getExpires(),
            authToken.getUsername()
        );
        VaadinSession.getCurrent().setAttribute(WebitelData.class, data);
    }

    public static WebitelData getWebitelAuthData() {
        if (VaadinSession.getCurrent() != null) {
            return VaadinSession.getCurrent().getAttribute(WebitelData.class);
        } else {
            return null;
        }
    }

    public static boolean isLoggedIn() {
        return getWebitelAuthData() != null && getWebitelAuthData().expires > TimeMachine.clock().millis();
    }

}
