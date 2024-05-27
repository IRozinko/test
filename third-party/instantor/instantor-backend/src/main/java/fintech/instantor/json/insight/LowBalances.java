
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "daysBalanceBelow7_3M",
    "daysBalanceBelow0_1W",
    "daysBalanceBelow0_3M",
    "daysBalanceBelow7_1W",
    "daysBalanceBelow0_12M",
    "version",
    "daysBalanceBelow0_6M",
    "daysBalanceBelow7_1M",
    "daysBalanceBelow7_Total",
    "daysBalanceBelow0_Total",
    "daysBalanceBelow0_1M",
    "daysBalanceBelow7_12M",
    "daysBalanceBelow7_6M"
})
@Data
public class LowBalances {

    private BigDecimal daysBalanceBelow7_3M;
    private BigDecimal daysBalanceBelow0_1W;
    private BigDecimal daysBalanceBelow0_3M;
    private BigDecimal daysBalanceBelow7_1W;
    private BigDecimal daysBalanceBelow0_12M;
    private String version;
    private BigDecimal daysBalanceBelow0_6M;
    private BigDecimal daysBalanceBelow7_1M;
    private BigDecimal daysBalanceBelow7_Total;
    private BigDecimal daysBalanceBelow0_Total;
    private BigDecimal daysBalanceBelow0_1M;
    private BigDecimal daysBalanceBelow7_12M;
    private BigDecimal daysBalanceBelow7_6M;
}
