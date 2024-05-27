package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "numDaysInOverdraft1W",
    "numDaysInOverdraft1M",
    "numOverdraft3M",
    "numOverdraft6M",
    "numDaysInOverdraft3M",
    "numDaysInOverdraftTotal",
    "numOverdraftTotal",
    "numOverdraft12M",
    "version",
    "numOverdraft1W",
    "numOverdraft1M",
    "numDaysInOverdraft12M",
    "numDaysInOverdraft6M"
})
@Data
public class Overdrafts {

    @JsonProperty("numDaysInOverdraft1W")
    public BigDecimal numDaysInOverdraft1W;
    @JsonProperty("numDaysInOverdraft1M")
    public BigDecimal numDaysInOverdraft1M;
    @JsonProperty("numOverdraft3M")
    public Integer numOverdraft3M;
    @JsonProperty("numOverdraft6M")
    public Integer numOverdraft6M;
    @JsonProperty("numDaysInOverdraft3M")
    public BigDecimal numDaysInOverdraft3M;
    @JsonProperty("numDaysInOverdraftTotal")
    public BigDecimal numDaysInOverdraftTotal;
    @JsonProperty("numOverdraftTotal")
    public Integer numOverdraftTotal;
    @JsonProperty("numOverdraft12M")
    public Integer numOverdraft12M;
    @JsonProperty("version")
    public String version;
    @JsonProperty("numOverdraft1W")
    public Integer numOverdraft1W;
    @JsonProperty("numOverdraft1M")
    public Integer numOverdraft1M;
    @JsonProperty("numDaysInOverdraft12M")
    public BigDecimal numDaysInOverdraft12M;
    @JsonProperty("numDaysInOverdraft6M")
    public BigDecimal numDaysInOverdraft6M;

}
