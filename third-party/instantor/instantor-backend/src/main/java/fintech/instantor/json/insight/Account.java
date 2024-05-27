
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
    "type",
    "balance",
    "currency",
    "iban",
    "holderName",
    "availableAmount",
    "transactionList"
})
@Data
public class Account {

    @JsonProperty("number")
    private String number;
    @JsonProperty("type")
    private String type;
    @JsonProperty("balance")
    private BigDecimal balance;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("iban")
    private String iban;
    @JsonProperty("holderName")
    private String holderName;
    @JsonProperty("availableAmount")
    private BigDecimal availableAmount;
    @JsonProperty("transactionList")
    private List<Transaction> transactionList = new ArrayList<>();
}
