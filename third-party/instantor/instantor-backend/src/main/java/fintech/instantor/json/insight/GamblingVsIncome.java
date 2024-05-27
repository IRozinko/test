
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gamblingIncomeRatio3M",
    "gamblingIncomeRatio1W",
    "sumGambling1W",
    "gamblingIncomeRatioTotal",
    "gamblingIncomeRatio12M",
    "sumGamblingTotal",
    "version",
    "gamblingIncomeRatio1M",
    "sumGambling6M",
    "sumGambling12M",
    "gamblingIncomeRatio6M",
    "sumGambling3M",
    "sumGambling1M"
})
@Data
public class GamblingVsIncome {

    @JsonProperty("gamblingIncomeRatio3M")
    private BigDecimal gamblingIncomeRatio3M;
    @JsonProperty("gamblingIncomeRatio1W")
    private BigDecimal gamblingIncomeRatio1W;
    @JsonProperty("sumGambling1W")
    private BigDecimal sumGambling1W;
    @JsonProperty("gamblingIncomeRatioTotal")
    private BigDecimal gamblingIncomeRatioTotal;
    @JsonProperty("gamblingIncomeRatio12M")
    private BigDecimal gamblingIncomeRatio12M;
    @JsonProperty("sumGamblingTotal")
    private BigDecimal sumGamblingTotal;
    @JsonProperty("version")
    private String version;
    @JsonProperty("gamblingIncomeRatio1M")
    private BigDecimal gamblingIncomeRatio1M;
    @JsonProperty("sumGambling6M")
    private BigDecimal sumGambling6M;
    @JsonProperty("sumGambling12M")
    private BigDecimal sumGambling12M;
    @JsonProperty("gamblingIncomeRatio6M")
    private BigDecimal gamblingIncomeRatio6M;
    @JsonProperty("sumGambling3M")
    private BigDecimal sumGambling3M;
    @JsonProperty("sumGambling1M")
    private BigDecimal sumGambling1M;
}
