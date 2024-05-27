
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "calendarMonth",
    "incoming",
    "outgoing",
    "minBalance",
    "maxBalance",
    "avgBalance",
    "isWholeMonth",
})
@Data
public class CashFlow {

    @JsonProperty("calendarMonth")
    private String calendarMonth;
    @JsonProperty("incoming")
    private BigDecimal incoming;
    @JsonProperty("outgoing")
    private BigDecimal outgoing;
    @JsonProperty("minBalance")
    private BigDecimal minBalance;
    @JsonProperty("maxBalance")
    private BigDecimal maxBalance;
    @JsonProperty("avgBalance")
    private BigDecimal avgBalance;
    @JsonProperty("isWholeMonth")
    private Boolean isWholeMonth;
}
