
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "atmExpensesRatioTotal",
    "sumAtmWithdrawals6M",
    "atmExpensesRatio1W",
    "atmExpensesRatio1M",
    "atmExpensesRatio6M",
    "sumAtmWithdrawals3M",
    "sumAtmWithdrawalsTotal",
    "atmExpensesRatio3M",
    "version",
    "sumAtmWithdrawals1W",
    "sumAtmWithdrawals1M",
    "sumAtmWithdrawals12M",
    "atmExpensesRatio12M"
})
@Data
public class AtmWithdrawals {
    private BigDecimal atmExpensesRatioTotal;
    private BigDecimal sumAtmWithdrawals6M;
    private BigDecimal atmExpensesRatio1W;
    private BigDecimal atmExpensesRatio1M;
    private BigDecimal atmExpensesRatio6M;
    private BigDecimal sumAtmWithdrawals3M;
    private BigDecimal sumAtmWithdrawalsTotal;
    private BigDecimal atmExpensesRatio3M;
    private String version;
    private BigDecimal sumAtmWithdrawals1W;
    private BigDecimal sumAtmWithdrawals1M;
    private BigDecimal sumAtmWithdrawals12M;
    private BigDecimal atmExpensesRatio12M;

}
