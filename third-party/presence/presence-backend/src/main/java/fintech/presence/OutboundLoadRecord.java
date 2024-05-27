package fintech.presence;

import fintech.presence.model.OutboundLoadRecordStatus;
import lombok.Data;

import java.util.List;

@Data
public class OutboundLoadRecord {
    private Long id;
    private Integer sourceId;
    private String name;
    private OutboundLoadRecordStatus status;
    private Integer qualificationCode;
    private OutboundLoad outboundLoad;
    private List<PhoneRecord> phoneRecords;
}
