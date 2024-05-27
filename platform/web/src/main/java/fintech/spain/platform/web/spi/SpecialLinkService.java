package fintech.spain.platform.web.spi;

import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.platform.web.model.SpecialLink;
import fintech.spain.platform.web.model.command.BuildLinkCommand;
import fintech.spain.platform.web.model.command.SpecialLinkQuery;

import java.util.Map;
import java.util.Optional;

public interface SpecialLinkService {

    SpecialLink buildLink(BuildLinkCommand command);

    Optional<SpecialLink> findLink(SpecialLinkQuery query);

    SpecialLink findRequiredLink(SpecialLinkQuery query);

    SpecialLink activateLink(String token);

    SpecialLink activateLink(String token, Map<String, Object> activationParameters);

    boolean deactivateLink(long clientId, SpecialLinkType type);

    boolean isExpired(String token);
}
