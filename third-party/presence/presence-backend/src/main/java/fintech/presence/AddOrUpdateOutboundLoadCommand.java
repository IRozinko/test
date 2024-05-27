package fintech.presence;

import fintech.presence.model.OutboundLoadStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AddOrUpdateOutboundLoadCommand {

    private Integer serviceId;
    private Integer loadId;
    private OutboundLoadStatus status;
    private LocalDateTime addedAt;
    private String description;
}
