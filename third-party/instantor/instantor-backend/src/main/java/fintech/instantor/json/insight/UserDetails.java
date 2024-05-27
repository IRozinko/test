
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "address",
    "phone",
    "email",
    "personalIdentifier"
})
@Data
public class UserDetails {

    @JsonProperty("name")
    private String name;
    @JsonProperty("address")
    private List<String> address;
    @JsonProperty("phone")
    private List<String> phone;
    @JsonProperty("email")
    private List<String> email;
    @JsonProperty("personalIdentifier")
    private List<EntityPair> personalIdentifier;

}
