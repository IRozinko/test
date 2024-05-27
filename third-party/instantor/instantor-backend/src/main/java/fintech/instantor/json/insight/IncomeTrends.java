
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "avg30dRollingRecent",
    "avg30dRollingPast",
    "version",
    "avgRollingTrend",
    "avgRollingDiff",
})
@Data
public class IncomeTrends {

    @JsonProperty("avg30dRollingRecent")
    private BigDecimal avg30dRollingRecent;
    @JsonProperty("avg30dRollingPast")
    private BigDecimal avg30dRollingPast;
    @JsonProperty("version")
    private String version;
    @JsonProperty("avgRollingTrend")
    private BigDecimal avgRollingTrend;
    @JsonProperty("avgRollingDiff")
    private BigDecimal avgRollingDiff;
}
