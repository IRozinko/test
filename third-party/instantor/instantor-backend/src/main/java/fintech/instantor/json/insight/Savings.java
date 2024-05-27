
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sumSavings1W",
    "numSavings6M",
    "numSavingsTotal",
    "sumSavingsTotal",
    "sumSavings1M",
    "sumSavings3M",
    "sumSavings12M",
    "version",
    "numSavings12M",
    "numSavings3M",
    "numSavings1W",
    "sumSavings6M",
    "numSavings1M"
})
@Data
public class Savings {
    @JsonProperty("sumSavings1W")
    private BigDecimal sumSavings1W;
    @JsonProperty("numSavings6M")
    private BigDecimal numSavings6M;
    @JsonProperty("numSavingsTotal")
    private BigDecimal numSavingsTotal;
    @JsonProperty("sumSavingsTotal")
    private BigDecimal sumSavingsTotal;
    @JsonProperty("sumSavings1M")
    private BigDecimal sumSavings1M;
    @JsonProperty("sumSavings3M")
    private BigDecimal sumSavings3M;
    @JsonProperty("sumSavings12M")
    private BigDecimal sumSavings12M;
    @JsonProperty("version")
    private String version;
    @JsonProperty("numSavings12M")
    private BigDecimal numSavings12M;
    @JsonProperty("numSavings3M")
    private BigDecimal numSavings3M;
    @JsonProperty("numSavings1W")
    private BigDecimal numSavings1W;
    @JsonProperty("sumSavings6M")
    private BigDecimal sumSavings6M;
    @JsonProperty("numSavings1M")
    private BigDecimal numSavings1M;
}
