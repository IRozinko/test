
package fintech.nordigen.json;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "partner",
    "info",
    "sum",
    "category"
})
public class TransactionList {

    @JsonProperty("date")
    private String date;
    @JsonProperty("partner")
    private String partner;
    @JsonProperty("info")
    private String info;
    @JsonProperty("sum")
    private Double sum;
    @JsonProperty("category")
    private Category category;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("partner")
    public String getPartner() {
        return partner;
    }

    @JsonProperty("partner")
    public void setPartner(String partner) {
        this.partner = partner;
    }

    @JsonProperty("info")
    public String getInfo() {
        return info;
    }

    @JsonProperty("info")
    public void setInfo(String info) {
        this.info = info;
    }

    @JsonProperty("sum")
    public Double getSum() {
        return sum;
    }

    @JsonProperty("sum")
    public void setSum(Double sum) {
        this.sum = sum;
    }

    @JsonProperty("category")
    public Category getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(Category category) {
        this.category = category;
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
