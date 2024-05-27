
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fintech.instantor.json.InstantorResponseJson;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "instantorRequestId",
    "processFinishedTime",
    "processStartTime",
    "processStatus",
    "reportNumber",
    "miscParams",
    "accountReportList",
    "userDetails",
    "bankInfo",
    "accountList",
    "incomeVerification",
    "insightsRiskFeatures"
})
@Data
public class InstantorInsightResponse implements InstantorResponseJson {

    @JsonProperty("instantorRequestId")
    private String instantorRequestId;
    @JsonProperty("processFinishedTime")
    private String processFinishedTime;
    @JsonProperty("processStartTime")
    private String processStartTime;
    @JsonProperty("processStatus")
    private String processStatus;
    @JsonProperty("reportNumber")
    private Integer reportNumber;
    @JsonProperty("miscParams")
    private List<MiscParam> miscParams = new ArrayList<>();
    @JsonProperty("accountReportList")
    private List<AccountReport> accountReportList = new ArrayList<>();
    @JsonProperty("userDetails")
    private UserDetails userDetails;
    @JsonProperty("bankInfo")
    private BankInfo bankInfo;
    @JsonProperty("accountList")
    private List<Account> accountList = new ArrayList<>();
    @JsonProperty("incomeVerification")
    private IncomeVerification incomeVerification;
    @JsonProperty("insightsRiskFeatures")
    private InsightsRiskFeatures insightsRiskFeatures;

}
