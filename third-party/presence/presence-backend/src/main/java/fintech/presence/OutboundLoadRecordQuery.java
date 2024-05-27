package fintech.presence;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OutboundLoadRecordQuery {

    private Integer serviceId;
    private Integer loadId;
    private Integer sourceId;
}
