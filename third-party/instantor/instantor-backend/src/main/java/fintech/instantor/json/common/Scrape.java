
package fintech.instantor.json.common;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "scrapeReport",
    "bank",
    "userDetails",
    "companyDetails",
    "accountList"
})
public class Scrape {

    @JsonProperty("scrapeReport")
    private ScrapeReport_ scrapeReport;
    @JsonProperty("bank")
    private Bank__ bank;
    @JsonProperty("userDetails")
    private UserDetails userDetails;
    @JsonProperty("companyDetails")
    private Object companyDetails;
    @JsonProperty("accountList")
    private List<AccountList> accountList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("scrapeReport")
    public ScrapeReport_ getScrapeReport() {
        return scrapeReport;
    }

    @JsonProperty("scrapeReport")
    public void setScrapeReport(ScrapeReport_ scrapeReport) {
        this.scrapeReport = scrapeReport;
    }

    @JsonProperty("bank")
    public Bank__ getBank() {
        return bank;
    }

    @JsonProperty("bank")
    public void setBank(Bank__ bank) {
        this.bank = bank;
    }

    @JsonProperty("userDetails")
    public UserDetails getUserDetails() {
        return userDetails;
    }

    @JsonProperty("userDetails")
    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @JsonProperty("companyDetails")
    public Object getCompanyDetails() {
        return companyDetails;
    }

    @JsonProperty("companyDetails")
    public void setCompanyDetails(Object companyDetails) {
        this.companyDetails = companyDetails;
    }

    @JsonProperty("accountList")
    public List<AccountList> getAccountList() {
        return accountList;
    }

    @JsonProperty("accountList")
    public void setAccountList(List<AccountList> accountList) {
        this.accountList = accountList;
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
