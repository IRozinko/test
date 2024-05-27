package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class GetOutboundLoadRecordsResponse extends PresenceResponse<List<GetOutboundLoadRecordsResponse.OutboundLoadRecordData>> {

    @Data
    @NoArgsConstructor
    public static class OutboundLoadRecordData {

        @JsonProperty("LoadId")
        private Integer loadId;

        @JsonProperty("ServiceId")
        private Integer serviceId;

        @JsonProperty("SourceId")
        private Integer sourceId;

        @JsonProperty("Status")
        private OutboundLoadRecordStatus status;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("PhoneNumber")
        private String phoneNumber;
    }
}

