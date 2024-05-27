package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {

    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;
}
