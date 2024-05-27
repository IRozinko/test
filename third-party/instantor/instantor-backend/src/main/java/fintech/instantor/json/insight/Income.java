
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "meanAmountPayment",
    "last45dFound",
    "avg6m",
    "avg3m",
    "meanTimeBetweenPayments",
    "trend3m",
    "daysAgoLastPayment",
    "trend6m",
    "amountLastPayment",
    "spanMonths",
    "trend12m",
    "avg12m",
    "numPayments",
    "descriptions",
})
@Data
public class Income {

    @JsonProperty("meanAmountPayment")
    private BigDecimal meanAmountPayment;
    @JsonProperty("last45dFound")
    private Integer last45dFound;
    @JsonProperty("avg6m")
    private BigDecimal avg6m;
    @JsonProperty("avg3m")
    private BigDecimal avg3m;
    @JsonProperty("meanTimeBetweenPayments")
    private Integer meanTimeBetweenPayments;
    @JsonProperty("trend3m")
    private BigDecimal trend3m;
    @JsonProperty("daysAgoLastPayment")
    private Integer daysAgoLastPayment;
    @JsonProperty("trend6m")
    private BigDecimal trend6m;
    @JsonProperty("amountLastPayment")
    private BigDecimal amountLastPayment;
    @JsonProperty("spanMonths")
    private BigDecimal spanMonths;
    @JsonProperty("trend12m")
    private BigDecimal trend12m;
    @JsonProperty("avg12m")
    private BigDecimal avg12m;
    @JsonProperty("numPayments")
    private Integer numPayments;
    @JsonProperty("descriptions")
    private List<String> descriptions;
}
