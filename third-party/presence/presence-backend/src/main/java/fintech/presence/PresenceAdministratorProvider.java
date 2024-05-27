package fintech.presence;

import fintech.presence.model.GetOutboundLoadRecordInfoResponse;
import fintech.presence.model.GetOutboundLoadRecordsResponse;
import fintech.presence.model.GetOutboundLoadsResponse;
import fintech.presence.model.GetOutboundServiceInfoResponse;
import fintech.presence.model.GetPhoneDescriptionsResponse;
import fintech.presence.model.GetTokenResponse;
import fintech.presence.model.RemoveRecordFromOutboundLoadResponse;

import java.io.IOException;
import java.util.List;

public interface PresenceAdministratorProvider {
    GetTokenResponse getToken() throws PresenceException, IOException;

    void login(String token, String user, String password) throws IOException, PresenceException;

    void logout(String token) throws IOException, PresenceException;

    GetOutboundLoadsResponse getOutboundLoads(String token, Integer serviceId) throws IOException, PresenceException;

    GetOutboundLoadRecordsResponse getOutboundLoadRecords(String token, Integer serviceId, Integer loadId) throws IOException, PresenceException;

    GetOutboundServiceInfoResponse getOutboundServiceInfo(String token, Integer serviceId) throws IOException, PresenceException;

    GetOutboundLoadRecordInfoResponse getOutboundLoadRecordInfo(String token, Integer serviceId, Integer loadId, Integer sourceId) throws IOException, PresenceException;

    void addRecordToOutboundLoad(String token, Integer serviceId, Integer loadId, Integer sourceId, String name, String customerId, List<PhoneRecord> phoneRecords) throws IOException, PresenceException;

    RemoveRecordFromOutboundLoadResponse removeRecordFromOutboundLoad(String token, Integer serviceId, Integer loadId, Integer sourceId) throws IOException, PresenceException;

    GetPhoneDescriptionsResponse getPhoneDescriptions(String token) throws IOException, PresenceException;
}
