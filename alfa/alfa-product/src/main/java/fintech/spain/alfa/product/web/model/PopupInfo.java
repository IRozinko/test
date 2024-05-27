package fintech.spain.alfa.product.web.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Accessors(chain = true)
public class PopupInfo {
    private long id;
    private long clientId;
    private PopupType type;
    private PopupResolution resolution;
    private LocalDateTime resolvedAt;
    private LocalDateTime validUntil;
    private Map<String, String> attributes;
}
