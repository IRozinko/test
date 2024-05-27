
package fintech.instantor.json.common;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "bank",
    "owner",
    "scrapeStatus",
    "monthsAvailable",
    "numberOfAccounts",
    "accountNumbersVerified",
    "nameVerified",
    "personalNumberVerified",
    "totalNumberOfTransactions",
    "averageNumberOfTransactionsMonth",
    "averageAmountOfIncomingTransactionsMonth",
    "averageAmountOfOutgoingTransactionsMonth",
    "averageMinimumBalanceMonth",
    "numberOfLoans",
    "amountOfLoans",
    "cashFlow"
})
public class BankReport {

    @JsonProperty("bank")
    private Bank_ bank;
    @JsonProperty("owner")
    private String owner;
    @JsonProperty("scrapeStatus")
    private String scrapeStatus;
    @JsonProperty("monthsAvailable")
    private Integer monthsAvailable;
    @JsonProperty("numberOfAccounts")
    private Integer numberOfAccounts;
    @JsonProperty("accountNumbersVerified")
    private List<AccountNumbersVerified> accountNumbersVerified = null;
    @JsonProperty("nameVerified")
    private Boolean nameVerified;
    @JsonProperty("personalNumberVerified")
    private Object personalNumberVerified;
    @JsonProperty("totalNumberOfTransactions")
    private Integer totalNumberOfTransactions;
    @JsonProperty("averageNumberOfTransactionsMonth")
    private Integer averageNumberOfTransactionsMonth;
    @JsonProperty("averageAmountOfIncomingTransactionsMonth")
    private Double averageAmountOfIncomingTransactionsMonth;
    @JsonProperty("averageAmountOfOutgoingTransactionsMonth")
    private Double averageAmountOfOutgoingTransactionsMonth;
    @JsonProperty("averageMinimumBalanceMonth")
    private Double averageMinimumBalanceMonth;
    @JsonProperty("numberOfLoans")
    private Integer numberOfLoans;
    @JsonProperty("amountOfLoans")
    private Integer amountOfLoans;
    @JsonProperty("cashFlow")
    private List<CashFlow> cashFlow = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("bank")
    public Bank_ getBank() {
        return bank;
    }

    @JsonProperty("bank")
    public void setBank(Bank_ bank) {
        this.bank = bank;
    }

    @JsonProperty("owner")
    public String getOwner() {
        return owner;
    }

    @JsonProperty("owner")
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @JsonProperty("scrapeStatus")
    public String getScrapeStatus() {
        return scrapeStatus;
    }

    @JsonProperty("scrapeStatus")
    public void setScrapeStatus(String scrapeStatus) {
        this.scrapeStatus = scrapeStatus;
    }

    @JsonProperty("monthsAvailable")
    public Integer getMonthsAvailable() {
        return monthsAvailable;
    }

    @JsonProperty("monthsAvailable")
    public void setMonthsAvailable(Integer monthsAvailable) {
        this.monthsAvailable = monthsAvailable;
    }

    @JsonProperty("numberOfAccounts")
    public Integer getNumberOfAccounts() {
        return numberOfAccounts;
    }

    @JsonProperty("numberOfAccounts")
    public void setNumberOfAccounts(Integer numberOfAccounts) {
        this.numberOfAccounts = numberOfAccounts;
    }

    @JsonProperty("accountNumbersVerified")
    public List<AccountNumbersVerified> getAccountNumbersVerified() {
        return accountNumbersVerified;
    }

    @JsonProperty("accountNumbersVerified")
    public void setAccountNumbersVerified(List<AccountNumbersVerified> accountNumbersVerified) {
        this.accountNumbersVerified = accountNumbersVerified;
    }

    @JsonProperty("nameVerified")
    public Boolean getNameVerified() {
        return nameVerified;
    }

    @JsonProperty("nameVerified")
    public void setNameVerified(Boolean nameVerified) {
        this.nameVerified = nameVerified;
    }

    @JsonProperty("personalNumberVerified")
    public Object getPersonalNumberVerified() {
        return personalNumberVerified;
    }

    @JsonProperty("personalNumberVerified")
    public void setPersonalNumberVerified(Object personalNumberVerified) {
        this.personalNumberVerified = personalNumberVerified;
    }

    @JsonProperty("totalNumberOfTransactions")
    public Integer getTotalNumberOfTransactions() {
        return totalNumberOfTransactions;
    }

    @JsonProperty("totalNumberOfTransactions")
    public void setTotalNumberOfTransactions(Integer totalNumberOfTransactions) {
        this.totalNumberOfTransactions = totalNumberOfTransactions;
    }

    @JsonProperty("averageNumberOfTransactionsMonth")
    public Integer getAverageNumberOfTransactionsMonth() {
        return averageNumberOfTransactionsMonth;
    }

    @JsonProperty("averageNumberOfTransactionsMonth")
    public void setAverageNumberOfTransactionsMonth(Integer averageNumberOfTransactionsMonth) {
        this.averageNumberOfTransactionsMonth = averageNumberOfTransactionsMonth;
    }

    @JsonProperty("averageAmountOfIncomingTransactionsMonth")
    public Double getAverageAmountOfIncomingTransactionsMonth() {
        return averageAmountOfIncomingTransactionsMonth;
    }

    @JsonProperty("averageAmountOfIncomingTransactionsMonth")
    public void setAverageAmountOfIncomingTransactionsMonth(Double averageAmountOfIncomingTransactionsMonth) {
        this.averageAmountOfIncomingTransactionsMonth = averageAmountOfIncomingTransactionsMonth;
    }

    @JsonProperty("averageAmountOfOutgoingTransactionsMonth")
    public Double getAverageAmountOfOutgoingTransactionsMonth() {
        return averageAmountOfOutgoingTransactionsMonth;
    }

    @JsonProperty("averageAmountOfOutgoingTransactionsMonth")
    public void setAverageAmountOfOutgoingTransactionsMonth(Double averageAmountOfOutgoingTransactionsMonth) {
        this.averageAmountOfOutgoingTransactionsMonth = averageAmountOfOutgoingTransactionsMonth;
    }

    @JsonProperty("averageMinimumBalanceMonth")
    public Double getAverageMinimumBalanceMonth() {
        return averageMinimumBalanceMonth;
    }

    @JsonProperty("averageMinimumBalanceMonth")
    public void setAverageMinimumBalanceMonth(Double averageMinimumBalanceMonth) {
        this.averageMinimumBalanceMonth = averageMinimumBalanceMonth;
    }

    @JsonProperty("numberOfLoans")
    public Integer getNumberOfLoans() {
        return numberOfLoans;
    }

    @JsonProperty("numberOfLoans")
    public void setNumberOfLoans(Integer numberOfLoans) {
        this.numberOfLoans = numberOfLoans;
    }

    @JsonProperty("amountOfLoans")
    public Integer getAmountOfLoans() {
        return amountOfLoans;
    }

    @JsonProperty("amountOfLoans")
    public void setAmountOfLoans(Integer amountOfLoans) {
        this.amountOfLoans = amountOfLoans;
    }

    @JsonProperty("cashFlow")
    public List<CashFlow> getCashFlow() {
        return cashFlow;
    }

    @JsonProperty("cashFlow")
    public void setCashFlow(List<CashFlow> cashFlow) {
        this.cashFlow = cashFlow;
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
