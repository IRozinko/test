package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "activityIncTotal",
    "activity12M",
    "activityInc1W",
    "activity1W",
    "activity3M",
    "activity1M",
    "trnsPerDay",
    "activityInc12M",
    "daysSinceLastTrn",
    "activityOut12M",
    "activityInc1M",
    "activityOut3M",
    "version",
    "daysOfOutTrns",
    "activityInc3M",
    "activityInc6M",
    "daysOfTrns",
    "activityOut6M",
    "activityOut1W",
    "activityOut1M",
    "daysSinceFirstTrn",
    "activityOutTotal",
    "activityTotal",
    "activity6M",
    "daysOfIncTrns"
})
@Data
public class AccountActivity {

    @JsonProperty("activityIncTotal")
    public BigDecimal activityIncTotal;
    @JsonProperty("activity12M")
    public BigDecimal activity12M;
    @JsonProperty("activityInc1W")
    public BigDecimal activityInc1W;
    @JsonProperty("activity1W")
    public BigDecimal activity1W;
    @JsonProperty("activity3M")
    public BigDecimal activity3M;
    @JsonProperty("activity1M")
    public BigDecimal activity1M;
    @JsonProperty("trnsPerDay")
    public BigDecimal trnsPerDay;
    @JsonProperty("activityInc12M")
    public BigDecimal activityInc12M;
    @JsonProperty("daysSinceLastTrn")
    public Integer daysSinceLastTrn;
    @JsonProperty("activityOut12M")
    public BigDecimal activityOut12M;
    @JsonProperty("activityInc1M")
    public BigDecimal activityInc1M;
    @JsonProperty("activityOut3M")
    public BigDecimal activityOut3M;
    @JsonProperty("version")
    public String version;
    @JsonProperty("daysOfOutTrns")
    public Integer daysOfOutTrns;
    @JsonProperty("activityInc3M")
    public BigDecimal activityInc3M;
    @JsonProperty("activityInc6M")
    public BigDecimal activityInc6M;
    @JsonProperty("daysOfTrns")
    public Integer daysOfTrns;
    @JsonProperty("activityOut6M")
    public BigDecimal activityOut6M;
    @JsonProperty("activityOut1W")
    public BigDecimal activityOut1W;
    @JsonProperty("activityOut1M")
    public BigDecimal activityOut1M;
    @JsonProperty("daysSinceFirstTrn")
    public Integer daysSinceFirstTrn;
    @JsonProperty("activityOutTotal")
    public BigDecimal activityOutTotal;
    @JsonProperty("activityTotal")
    public BigDecimal activityTotal;
    @JsonProperty("activity6M")
    public BigDecimal activity6M;
    @JsonProperty("daysOfIncTrns")
    public Integer daysOfIncTrns;

}
