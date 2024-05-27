package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class GetPhoneDescriptionsResponse extends PresenceResponse<List<GetPhoneDescriptionsResponse.PhoneDescription>> {

    @Data
    @NoArgsConstructor
    public static class PhoneDescription {

        @JsonProperty("Code")
        private Integer code;

        @JsonProperty("Description")
        private String description;
    }

}
