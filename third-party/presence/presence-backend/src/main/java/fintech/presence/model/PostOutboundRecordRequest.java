package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PostOutboundRecordRequest {

    @JsonProperty("SourceId")
    private Integer sourceId;

    @JsonProperty("Name")
    private String name;

    @JsonUnwrapped
    private PhoneRecordsWrapper phoneRecordsWrapper;

    @JsonProperty("Priority")
    private Integer priority;

    @JsonProperty("Comments")
    private String comments;

    @JsonProperty("Scheduled")
    private Boolean scheduled;

    @JsonProperty("ScheduleDate")
    private String scheduleDate;

    @JsonProperty("ScheduleTime")
    private String scheduleTime;

    @JsonProperty("CapturingAgent")
    private Integer capturingAgent;

    @JsonProperty("CustomData1")
    private String customData1;

    @JsonProperty("CustomData2")
    private String customData2;

    @JsonProperty("CustomData3")
    private String customData3;

    @JsonProperty("CallerId")
    private String callerId;

    @JsonProperty("CallerName")
    private String callerName;

    @JsonProperty("CustomerId")
    private String customerId;
}
