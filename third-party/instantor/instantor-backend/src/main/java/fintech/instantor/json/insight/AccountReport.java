
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "number",
    "totalNumberOfTransactions",
    "wholeMonthsAvailable",
    "averageNumberOfTransactionsWholeMonth",
    "averageAmountOfIncomingTransactionsWholeMonth",
    "averageAmountOfOutgoingTransactionsWholeMonth",
    "averageMinimumBalanceWholeMonth",
    "cashFlow"
})
@Data
public class AccountReport {

    @JsonProperty("number")
    private String number;
    @JsonProperty("totalNumberOfTransactions")
    private Integer totalNumberOfTransactions;
    @JsonProperty("wholeMonthsAvailable")
    private Integer wholeMonthsAvailable;
    @JsonProperty("averageNumberOfTransactionsWholeMonth")
    private BigDecimal averageNumberOfTransactionsWholeMonth;
    @JsonProperty("averageAmountOfIncomingTransactionsWholeMonth")
    private BigDecimal averageAmountOfIncomingTransactionsWholeMonth;
    @JsonProperty("averageAmountOfOutgoingTransactionsWholeMonth")
    private BigDecimal averageAmountOfOutgoingTransactionsWholeMonth;
    @JsonProperty("averageMinimumBalanceWholeMonth")
    private BigDecimal averageMinimumBalanceWholeMonth;
    @JsonProperty("cashFlow")
    private List<CashFlow> cashFlows = new ArrayList<>();

}
