package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PresenceResponse<T> {

    @JsonProperty("Code")
    private int code;

    @JsonProperty("ErrorMessage")
    private String errorMessage;

    @JsonProperty("Data")
    private T data;
}
