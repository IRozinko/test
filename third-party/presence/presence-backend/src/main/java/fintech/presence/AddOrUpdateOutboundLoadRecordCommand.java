package fintech.presence;

import fintech.presence.model.OutboundLoadRecordStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AddOrUpdateOutboundLoadRecordCommand {

    private Long outboundLoadId;
    private Long outboundLoadRecordId;
    private Integer sourceId;
    private String name;
    private OutboundLoadRecordStatus status;
    private Integer qualificationCode;
    private List<PhoneRecord> phoneRecords;
}
