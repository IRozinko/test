
package fintech.nordigen.json;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "account_number",
    "holder_name",
    "bank_name",
    "currency",
    "period_start",
    "period_end",
    "transaction_list",
    "red_flags",
    "pink_flags",
    "factors"
})
public class AccountList {

    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("holder_name")
    private String holderName;
    @JsonProperty("bank_name")
    private String bankName;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("period_start")
    private String periodStart;
    @JsonProperty("period_end")
    private String periodEnd;
    @JsonProperty("transaction_list")
    private List<TransactionList> transactionList = null;
    @JsonProperty("red_flags")
    private List<String> redFlags = null;
    @JsonProperty("pink_flags")
    private List<Object> pinkFlags = null;
    @JsonProperty("factors")
    private Factors factors;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("account_number")
    public String getAccountNumber() {
        return accountNumber;
    }

    @JsonProperty("account_number")
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @JsonProperty("holder_name")
    public String getHolderName() {
        return holderName;
    }

    @JsonProperty("holder_name")
    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    @JsonProperty("bank_name")
    public String getBankName() {
        return bankName;
    }

    @JsonProperty("bank_name")
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("period_start")
    public String getPeriodStart() {
        return periodStart;
    }

    @JsonProperty("period_start")
    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }

    @JsonProperty("period_end")
    public String getPeriodEnd() {
        return periodEnd;
    }

    @JsonProperty("period_end")
    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }

    @JsonProperty("transaction_list")
    public List<TransactionList> getTransactionList() {
        return transactionList;
    }

    @JsonProperty("transaction_list")
    public void setTransactionList(List<TransactionList> transactionList) {
        this.transactionList = transactionList;
    }

    @JsonProperty("red_flags")
    public List<String> getRedFlags() {
        return redFlags;
    }

    @JsonProperty("red_flags")
    public void setRedFlags(List<String> redFlags) {
        this.redFlags = redFlags;
    }

    @JsonProperty("pink_flags")
    public List<Object> getPinkFlags() {
        return pinkFlags;
    }

    @JsonProperty("pink_flags")
    public void setPinkFlags(List<Object> pinkFlags) {
        this.pinkFlags = pinkFlags;
    }

    @JsonProperty("factors")
    public Factors getFactors() {
        return factors;
    }

    @JsonProperty("factors")
    public void setFactors(Factors factors) {
        this.factors = factors;
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
