package fintech.presence;

import fintech.presence.model.OutboundLoadStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OutboundLoadQuery {

    private Integer serviceId;
    private Integer loadId;
    private OutboundLoadStatus outboundLoadStatus;
}
