
package fintech.instantor.json.common;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "scrapeDate",
    "status",
    "comment",
    "reportNumber",
    "isFinal"
})
public class ScrapeReport {

    @JsonProperty("scrapeDate")
    private String scrapeDate;
    @JsonProperty("status")
    private String status;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("reportNumber")
    private Integer reportNumber;
    @JsonProperty("isFinal")
    private Boolean isFinal;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("scrapeDate")
    public String getScrapeDate() {
        return scrapeDate;
    }

    @JsonProperty("scrapeDate")
    public void setScrapeDate(String scrapeDate) {
        this.scrapeDate = scrapeDate;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("reportNumber")
    public Integer getReportNumber() {
        return reportNumber;
    }

    @JsonProperty("reportNumber")
    public void setReportNumber(Integer reportNumber) {
        this.reportNumber = reportNumber;
    }

    @JsonProperty("isFinal")
    public Boolean getIsFinal() {
        return isFinal;
    }

    @JsonProperty("isFinal")
    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
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
