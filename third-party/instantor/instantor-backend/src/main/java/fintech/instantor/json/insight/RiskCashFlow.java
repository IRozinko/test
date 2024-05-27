
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "positiveCashflow1M",
    "positiveCashflow1W",
    "negativeCashflow3M",
    "positiveCashflowTotal",
    "positiveCashflow3M",
    "negativeCashflowTotal",
    "positiveNegativeRatio6M",
    "positiveNegativeRatioTotal",
    "negativeCashflow1W",
    "positiveNegativeRatio3M",
    "negativeCashflow12M",
    "negativeCashflow1M",
    "positiveNegativeRatio1W",
    "positiveNegativeRatio1M",
    "version",
    "positiveCashflow6M",
    "positiveCashflow12M",
    "positiveNegativeRatio12M",
    "negativeCashflow6M"
})
@Data
public class RiskCashFlow {

    @JsonProperty("positiveCashflow1M")
    private BigDecimal positiveCashflow1M;
    @JsonProperty("positiveCashflow1W")
    private BigDecimal positiveCashflow1W;
    @JsonProperty("negativeCashflow3M")
    private BigDecimal negativeCashflow3M;
    @JsonProperty("positiveCashflowTotal")
    private BigDecimal positiveCashflowTotal;
    @JsonProperty("positiveCashflow3M")
    private BigDecimal positiveCashflow3M;
    @JsonProperty("negativeCashflowTotal")
    private BigDecimal negativeCashflowTotal;
    @JsonProperty("positiveNegativeRatio6M")
    private BigDecimal positiveNegativeRatio6M;
    @JsonProperty("positiveNegativeRatioTotal")
    private BigDecimal positiveNegativeRatioTotal;
    @JsonProperty("negativeCashflow1W")
    private BigDecimal negativeCashflow1W;
    @JsonProperty("positiveNegativeRatio3M")
    private BigDecimal positiveNegativeRatio3M;
    @JsonProperty("negativeCashflow12M")
    private BigDecimal negativeCashflow12M;
    @JsonProperty("negativeCashflow1M")
    private BigDecimal negativeCashflow1M;
    @JsonProperty("positiveNegativeRatio1W")
    private BigDecimal positiveNegativeRatio1W;
    @JsonProperty("positiveNegativeRatio1M")
    private BigDecimal positiveNegativeRatio1M;
    @JsonProperty("version")
    private String version;
    @JsonProperty("positiveCashflow6M")
    private BigDecimal positiveCashflow6M;
    @JsonProperty("positiveCashflow12M")
    private BigDecimal positiveCashflow12M;
    @JsonProperty("positiveNegativeRatio12M")
    private BigDecimal positiveNegativeRatio12M;
    @JsonProperty("negativeCashflow6M")
    private BigDecimal negativeCashflow6M;

}
