
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "onDate",
    "valueDate",
    "description",
    "amount",
    "balance",
    "params"
})
@Data
public class Transaction {
    @JsonProperty("onDate")
    private String onDate;
    @JsonProperty("valueDate")
    private String valueDate;
    @JsonProperty("description")
    private String description;
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("params")
    private List<Map<String, String>> params;
}
