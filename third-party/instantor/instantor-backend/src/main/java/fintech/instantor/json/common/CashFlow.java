
package fintech.instantor.json.common;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "month",
    "incoming",
    "outgoing",
    "minBalance",
    "numberOfLoans",
    "amountOfLoans"
})
public class CashFlow {

    @JsonProperty("month")
    private Integer month;
    @JsonProperty("incoming")
    private Double incoming;
    @JsonProperty("outgoing")
    private Double outgoing;
    @JsonProperty("minBalance")
    private Double minBalance;
    @JsonProperty("numberOfLoans")
    private Integer numberOfLoans;
    @JsonProperty("amountOfLoans")
    private Integer amountOfLoans;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("month")
    public Integer getMonth() {
        return month;
    }

    @JsonProperty("month")
    public void setMonth(Integer month) {
        this.month = month;
    }

    @JsonProperty("incoming")
    public Double getIncoming() {
        return incoming;
    }

    @JsonProperty("incoming")
    public void setIncoming(Double incoming) {
        this.incoming = incoming;
    }

    @JsonProperty("outgoing")
    public Double getOutgoing() {
        return outgoing;
    }

    @JsonProperty("outgoing")
    public void setOutgoing(Double outgoing) {
        this.outgoing = outgoing;
    }

    @JsonProperty("minBalance")
    public Double getMinBalance() {
        return minBalance;
    }

    @JsonProperty("minBalance")
    public void setMinBalance(Double minBalance) {
        this.minBalance = minBalance;
    }

    @JsonProperty("numberOfLoans")
    public Integer getNumberOfLoans() {
        return numberOfLoans;
    }

    @JsonProperty("numberOfLoans")
    public void setNumberOfLoans(Integer numberOfLoans) {
        this.numberOfLoans = numberOfLoans;
    }

    @JsonProperty("amountOfLoans")
    public Integer getAmountOfLoans() {
        return amountOfLoans;
    }

    @JsonProperty("amountOfLoans")
    public void setAmountOfLoans(Integer amountOfLoans) {
        this.amountOfLoans = amountOfLoans;
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
