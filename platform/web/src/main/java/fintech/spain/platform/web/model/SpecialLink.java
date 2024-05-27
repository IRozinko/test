package fintech.spain.platform.web.model;

import fintech.spain.platform.web.SpecialLinkType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class SpecialLink {
    private Long id;
    private long clientId;
    private String token;
    private SpecialLinkType type;
    private LocalDateTime expiresAt;
    private boolean reusable;
    private boolean autoLoginRequired;
}
