
package fintech.nordigen.json;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "account_list"
})
public class NordigenJson {

    @JsonProperty("account_list")
    private List<AccountList> accountList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("account_list")
    public List<AccountList> getAccountList() {
        return accountList;
    }

    @JsonProperty("account_list")
    public void setAccountList(List<AccountList> accountList) {
        this.accountList = accountList;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
