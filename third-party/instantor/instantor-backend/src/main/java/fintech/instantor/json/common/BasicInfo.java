
package fintech.instantor.json.common;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "scrapeReport",
    "bank",
    "source",
    "username",
    "address",
    "phone",
    "email",
    "miscEntryList"
})
public class BasicInfo {

    @JsonProperty("scrapeReport")
    private ScrapeReport scrapeReport;
    @JsonProperty("bank")
    private Bank bank;
    @JsonProperty("source")
    private String source;
    @JsonProperty("username")
    private String username;
    @JsonProperty("address")
    private Object address;
    @JsonProperty("phone")
    private Object phone;
    @JsonProperty("email")
    private Object email;
    @JsonProperty("miscEntryList")
    private List<MiscEntryList> miscEntryList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("scrapeReport")
    public ScrapeReport getScrapeReport() {
        return scrapeReport;
    }

    @JsonProperty("scrapeReport")
    public void setScrapeReport(ScrapeReport scrapeReport) {
        this.scrapeReport = scrapeReport;
    }

    @JsonProperty("bank")
    public Bank getBank() {
        return bank;
    }

    @JsonProperty("bank")
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("address")
    public Object getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(Object address) {
        this.address = address;
    }

    @JsonProperty("phone")
    public Object getPhone() {
        return phone;
    }

    @JsonProperty("phone")
    public void setPhone(Object phone) {
        this.phone = phone;
    }

    @JsonProperty("email")
    public Object getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(Object email) {
        this.email = email;
    }

    @JsonProperty("miscEntryList")
    public List<MiscEntryList> getMiscEntryList() {
        return miscEntryList;
    }

    @JsonProperty("miscEntryList")
    public void setMiscEntryList(List<MiscEntryList> miscEntryList) {
        this.miscEntryList = miscEntryList;
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
