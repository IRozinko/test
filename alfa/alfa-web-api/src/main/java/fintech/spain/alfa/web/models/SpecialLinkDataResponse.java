package fintech.spain.alfa.web.models;

import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.platform.web.model.SpecialLink;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor
public class SpecialLinkDataResponse {
    private Long clientId;
    private SpecialLinkType type;
    private boolean autoLogin;
    private boolean reusable;
    Map<String, String> payload;

    public SpecialLinkDataResponse(SpecialLink link) {
        clientId = link.getClientId();
        type = link.getType();
        autoLogin = link.isAutoLoginRequired();
        reusable = link.isReusable();
        payload = Collections.emptyMap();
    }
}
