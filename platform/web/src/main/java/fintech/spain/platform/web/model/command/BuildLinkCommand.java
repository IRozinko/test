package fintech.spain.platform.web.model.command;

import fintech.spain.platform.web.SpecialLinkType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BuildLinkCommand {
    private long clientId;
    private SpecialLinkType type;
    private LocalDateTime expiresAt;
    private boolean reusable;
    private boolean autoLoginRequired;
}
