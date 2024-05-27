
package fintech.instantor.json.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fintech.instantor.json.InstantorResponseJson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "basicInfo",
    "reportTime",
    "scrapeTime",
    "successfulLogin",
    "accountNumbersForVerification",
    "nameForVerification",
    "personalNumberForVerification",
    "bankReport",
    "scrape"
})
@Deprecated
public class InstantorCommonResponse implements InstantorResponseJson {

    @JsonProperty("basicInfo")
    private BasicInfo basicInfo;
    @JsonProperty("reportTime")
    private String reportTime;
    @JsonProperty("scrapeTime")
    private String scrapeTime;
    @JsonProperty("successfulLogin")
    private Boolean successfulLogin;
    @JsonProperty("accountNumbersForVerification")
    private List<String> accountNumbersForVerification = null;
    @JsonProperty("nameForVerification")
    private String nameForVerification;
    @JsonProperty("personalNumberForVerification")
    private String personalNumberForVerification;
    @JsonProperty("bankReport")
    private BankReport bankReport;
    @JsonProperty("scrape")
    private Scrape scrape;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("basicInfo")
    public BasicInfo getBasicInfo() {
        return basicInfo;
    }

    @JsonProperty("basicInfo")
    public void setBasicInfo(BasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    @JsonProperty("reportTime")
    public String getReportTime() {
        return reportTime;
    }

    @JsonProperty("reportTime")
    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    @JsonProperty("scrapeTime")
    public String getScrapeTime() {
        return scrapeTime;
    }

    @JsonProperty("scrapeTime")
    public void setScrapeTime(String scrapeTime) {
        this.scrapeTime = scrapeTime;
    }

    @JsonProperty("successfulLogin")
    public Boolean getSuccessfulLogin() {
        return successfulLogin;
    }

    @JsonProperty("successfulLogin")
    public void setSuccessfulLogin(Boolean successfulLogin) {
        this.successfulLogin = successfulLogin;
    }

    @JsonProperty("accountNumbersForVerification")
    public List<String> getAccountNumbersForVerification() {
        return accountNumbersForVerification;
    }

    @JsonProperty("accountNumbersForVerification")
    public void setAccountNumbersForVerification(List<String> accountNumbersForVerification) {
        this.accountNumbersForVerification = accountNumbersForVerification;
    }

    @JsonProperty("nameForVerification")
    public String getNameForVerification() {
        return nameForVerification;
    }

    @JsonProperty("nameForVerification")
    public void setNameForVerification(String nameForVerification) {
        this.nameForVerification = nameForVerification;
    }

    @JsonProperty("personalNumberForVerification")
    public String getPersonalNumberForVerification() {
        return personalNumberForVerification;
    }

    @JsonProperty("personalNumberForVerification")
    public void setPersonalNumberForVerification(String personalNumberForVerification) {
        this.personalNumberForVerification = personalNumberForVerification;
    }

    @JsonProperty("bankReport")
    public BankReport getBankReport() {
        return bankReport;
    }

    @JsonProperty("bankReport")
    public void setBankReport(BankReport bankReport) {
        this.bankReport = bankReport;
    }

    @JsonProperty("scrape")
    public Scrape getScrape() {
        return scrape;
    }

    @JsonProperty("scrape")
    public void setScrape(Scrape scrape) {
        this.scrape = scrape;
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
