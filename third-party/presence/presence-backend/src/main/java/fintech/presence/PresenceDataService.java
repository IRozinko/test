package fintech.presence;

import java.util.List;

public interface PresenceDataService {
    OutboundLoadRecord getRecord(Long id);

    OutboundLoad getLoad(Long loadId);

    List<OutboundLoadRecord> findRecords(OutboundLoadRecordQuery query);

    List<OutboundLoad> findLoads(OutboundLoadQuery query);

    OutboundLoad addOrUpdateOutboundLoad(AddOrUpdateOutboundLoadCommand command);

    OutboundLoadRecord addOrUpdateOutboundLoadRecord(AddOrUpdateOutboundLoadRecordCommand command);

    Integer getNextSourceId();
}
