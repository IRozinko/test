
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "savings",
    "incomeTrends",
    "accountActivity",
    "cashFlow",
    "collections",
    "overdrafts",
    "lowBalances",
    "loans",
    "spendingDistribution",
    "accountOverview",
    "balances",
    "transactionStats",
    "gamblingVsIncome",
    "atmWithdrawals",
    "monthlyPaymentVariance"
})
@Data
public class InsightsRiskFeatures {

    @JsonProperty("savings")
    private Savings savings;
    @JsonProperty("incomeTrends")
    private IncomeTrends incomeTrends;

    @JsonProperty("accountActivity")
    private AccountActivity accountActivity;

    @JsonProperty("cashFlow")
    private RiskCashFlow cashFlow;
    @JsonProperty("collections")
    private Collections collections;

    @JsonProperty("overdrafts")
    private Overdrafts overdrafts;

    @JsonProperty("lowBalances")
    private LowBalances lowBalances;
    @JsonProperty("loans")
    private Loans loans;
    @JsonProperty("spendingDistribution")
    private SpendingDistribution spendingDistribution;

    @JsonProperty("accountOverview")
    private AccountOverview accountOverview;

    @JsonProperty("balances")
    private Balances balances;

    @JsonProperty("transactionStats")
    private TransactionStats transactionStats;

    @JsonProperty("gamblingVsIncome")
    private GamblingVsIncome gamblingVsIncome;
    @JsonProperty("atmWithdrawals")
    private AtmWithdrawals atmWithdrawals;
    @JsonProperty("monthlyPaymentVariance")
    private MonthlyPaymentVariance monthlyPaymentVariance;


}
