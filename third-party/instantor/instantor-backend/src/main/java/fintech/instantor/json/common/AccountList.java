
package fintech.instantor.json.common;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "number",
    "balance",
    "currency",
    "iban",
    "holderName",
    "transactionList"
})
public class AccountList {

    @JsonProperty("number")
    private String number;
    @JsonProperty("balance")
    private Double balance;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("iban")
    private String iban;
    @JsonProperty("holderName")
    private String holderName;
    @JsonProperty("transactionList")
    private List<TransactionList> transactionList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("number")
    public String getNumber() {
        return number;
    }

    @JsonProperty("number")
    public void setNumber(String number) {
        this.number = number;
    }

    @JsonProperty("balance")
    public Double getBalance() {
        return balance;
    }

    @JsonProperty("balance")
    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("iban")
    public String getIban() {
        return iban;
    }

    @JsonProperty("iban")
    public void setIban(String iban) {
        this.iban = iban;
    }

    @JsonProperty("holderName")
    public String getHolderName() {
        return holderName;
    }

    @JsonProperty("holderName")
    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    @JsonProperty("transactionList")
    public List<TransactionList> getTransactionList() {
        return transactionList;
    }

    @JsonProperty("transactionList")
    public void setTransactionList(List<TransactionList> transactionList) {
        this.transactionList = transactionList;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
