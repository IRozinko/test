
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "primaryIncome",
    "secondaryIncomes",
    "otherRecurIncome",
    "version"
})
@Data
public class IncomeVerification {

    @JsonProperty("primaryIncome")
    private Income primaryIncome;
    @JsonProperty("secondaryIncomes")
    private Income secondaryIncomes;
    @JsonProperty("otherRecurIncome")
    private Income otherRecurIncome;
    @JsonProperty("version")
    private String version;

}
