package fintech.presence;

import fintech.presence.model.OutboundLoadStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OutboundLoad {
    private Long id;
    private Integer loadId;
    private Integer serviceId;
    private OutboundLoadStatus status;
    private LocalDateTime addedAt;
    private String description;
}
