
package fintech.nordigen.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "group_food",
    "group_utilites",
    "group_utilities",
    "group_loans",
    "group_account_usage",
    "group_luxury",
    "group_all",
    "group_transfers",
    "group_other",
    "group_health",
    "group_financial_behavour",
    "group_financial_behaviour",
    "group_income",
    "group_cash"
})
public class Factors {

    @JsonProperty("group_food")
    private Double groupFood;
    @JsonProperty("group_utilites")
    private Double groupUtilites;
    @JsonProperty("group_utilities")
    private Double groupUtilities;
    @JsonProperty("group_loans")
    private Double groupLoans;
    @JsonProperty("group_account_usage")
    private Double groupAccountUsage;
    @JsonProperty("group_luxury")
    private Double groupLuxury;
    @JsonProperty("group_all")
    private Integer groupAll;
    @JsonProperty("group_transfers")
    private Double groupTransfers;
    @JsonProperty("group_other")
    private Double groupOther;
    @JsonProperty("group_health")
    private Double groupHealth;
    @JsonProperty("group_financial_behavour")
    private Double groupFinancialBehavour;
    @JsonProperty("group_financial_behaviour")
    private Double groupFinancialBehaviour;
    @JsonProperty("group_income")
    private Double groupIncome;
    @JsonProperty("group_cash")
    private Double groupCash;

    public Double getGroupFood() {
        return groupFood;
    }

    public void setGroupFood(Double groupFood) {
        this.groupFood = groupFood;
    }

    public Double getGroupUtilites() {
        return groupUtilites;
    }

    public void setGroupUtilites(Double groupUtilites) {
        this.groupUtilites = groupUtilites;
    }

    public Double getGroupUtilities() {
        return groupUtilities;
    }

    public void setGroupUtilities(Double groupUtilities) {
        this.groupUtilities = groupUtilities;
    }

    public Double getGroupLoans() {
        return groupLoans;
    }

    public void setGroupLoans(Double groupLoans) {
        this.groupLoans = groupLoans;
    }

    public Double getGroupAccountUsage() {
        return groupAccountUsage;
    }

    public void setGroupAccountUsage(Double groupAccountUsage) {
        this.groupAccountUsage = groupAccountUsage;
    }

    public Double getGroupLuxury() {
        return groupLuxury;
    }

    public void setGroupLuxury(Double groupLuxury) {
        this.groupLuxury = groupLuxury;
    }

    public Integer getGroupAll() {
        return groupAll;
    }

    public void setGroupAll(Integer groupAll) {
        this.groupAll = groupAll;
    }

    public Double getGroupTransfers() {
        return groupTransfers;
    }

    public void setGroupTransfers(Double groupTransfers) {
        this.groupTransfers = groupTransfers;
    }

    public Double getGroupOther() {
        return groupOther;
    }

    public void setGroupOther(Double groupOther) {
        this.groupOther = groupOther;
    }

    public Double getGroupHealth() {
        return groupHealth;
    }

    public void setGroupHealth(Double groupHealth) {
        this.groupHealth = groupHealth;
    }

    public Double getGroupFinancialBehavour() {
        return groupFinancialBehavour;
    }

    public void setGroupFinancialBehavour(Double groupFinancialBehavour) {
        this.groupFinancialBehavour = groupFinancialBehavour;
    }

    public Double getGroupFinancialBehaviour() {
        return groupFinancialBehaviour;
    }

    public void setGroupFinancialBehaviour(Double groupFinancialBehaviour) {
        this.groupFinancialBehaviour = groupFinancialBehaviour;
    }

    public Double getGroupIncome() {
        return groupIncome;
    }

    public void setGroupIncome(Double groupIncome) {
        this.groupIncome = groupIncome;
    }

    public Double getGroupCash() {
        return groupCash;
    }

    public void setGroupCash(Double groupCash) {
        this.groupCash = groupCash;
    }
}
