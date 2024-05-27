package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class GetOutboundLoadsResponse extends PresenceResponse<List<GetOutboundLoadsResponse.OutboundLoadData>> {

    @Data
    @NoArgsConstructor
    public static class OutboundLoadData {
        @JsonProperty("LoadId")
        private Integer loadId;

        @JsonProperty("ServiceId")
        private Integer serviceId;

        @JsonProperty("Date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime date;

        @JsonProperty("Enabled")
        private OutboundLoadStatus status;

        @JsonProperty("Description")
        private String description;

        @JsonProperty("PriorityValue")
        private String priorityValue;
    }

}


