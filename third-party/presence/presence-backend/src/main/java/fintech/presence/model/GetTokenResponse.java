package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

public class GetTokenResponse extends PresenceResponse<GetTokenResponse.TokenData> {

    @Data
    @NoArgsConstructor
    public static class TokenData {

        @JsonProperty("Token")
        private String token;
    }
}
