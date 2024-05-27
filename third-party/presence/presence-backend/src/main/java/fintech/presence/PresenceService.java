package fintech.presence;

import java.io.IOException;
import java.util.List;

public interface PresenceService {
    void updateCurrentOutboundLoad() throws IOException, PresenceException, PresenceOutboundLoadNotAvailable;

    void updateOutboundLoad(Integer serviceId, Integer loadId) throws IOException, PresenceException, PresenceOutboundLoadNotAvailable;

    Long addRecordToCurrentOutboundLoad(String name, String customerId, List<PhoneRecord> phoneRecords) throws IOException, PresenceException, PresenceOutboundLoadNotAvailable;

    void removeRecordFromOutboundLoad(Long id) throws IOException, PresenceException;
}
