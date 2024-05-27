package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "numAccounts",
    "numAccountTypes",
    "numAccountHolders",
    "numCurrencies",
    "version"
})
@Data
public class AccountOverview {

    @JsonProperty("numAccounts")
    public Integer numAccounts;
    @JsonProperty("numAccountTypes")
    public Integer numAccountTypes;
    @JsonProperty("numAccountHolders")
    public Integer numAccountHolders;
    @JsonProperty("numCurrencies")
    public Integer numCurrencies;
    @JsonProperty("version")
    public String version;

}
