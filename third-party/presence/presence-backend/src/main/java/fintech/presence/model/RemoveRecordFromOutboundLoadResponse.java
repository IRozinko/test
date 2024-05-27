package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RemoveRecordFromOutboundLoadResponse extends PresenceResponse<RemoveRecordFromOutboundLoadResponse.UnloadRecordInformation> {

    @Data
    @NoArgsConstructor
    public static class UnloadRecordInformation {

        @JsonProperty("TotalUnloadedRecord")
        private Integer totalUnloadedRecord;
    }

}
