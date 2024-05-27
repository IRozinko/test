package fintech.spain.platform.web.model.command;

import fintech.spain.platform.web.SpecialLinkType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SpecialLinkQuery {

    private Long clientId;
    private SpecialLinkType type;
    private String token;
    private Boolean onlyValid;

    public static SpecialLinkQuery byToken(String token) {
        SpecialLinkQuery query = new SpecialLinkQuery();
        query.token = token;
        return query;
    }

    public static SpecialLinkQuery byClientId(long clientId, SpecialLinkType type) {
        SpecialLinkQuery query = new SpecialLinkQuery();
        query.clientId = clientId;
        query.type = type;
        query.onlyValid = true;
        return query;
    }
}
