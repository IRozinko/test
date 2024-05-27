package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class GetOutboundLoadRecordInfoResponse extends PresenceResponse<List<GetOutboundLoadRecordInfoResponse.OutboundLoadRecordInfoData>> {

    @Data
    @NoArgsConstructor
    public static class OutboundLoadRecordInfoData {

        @JsonProperty("LoadId")
        private Integer loadId;

        @JsonProperty("ServiceId")
        private Integer serviceId;

        @JsonProperty("SourceId")
        private Integer sourceId;

        @JsonProperty("Status")
        private OutboundLoadRecordStatus status;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("PhoneNumber")
        private String phoneNumber;

        @JsonProperty("PhoneTimeZoneId")
        private String phoneTimeZoneId;

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

        @JsonProperty("LastQCode")
        private Integer lastQCode;

        @JsonProperty("DailyCounter")
        private Integer dailyCounter;

        @JsonProperty("TotalCounter")
        private Integer totalCounter;

        @JsonProperty("BusySignalCounter")
        private Integer busySignalCounter;

        @JsonProperty("NoAnswerCounter")
        private Integer noAnswerCounter;

        @JsonProperty("AnswerMachineCounter")
        private Integer answerMachineCounter;

        @JsonProperty("FaxCounter")
        private Integer faxCounter;

        @JsonProperty("InvGenReasonCounter")
        private Integer invGenReasonCounter;

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

        @JsonProperty("TimeZoneId")
        private String timeZoneId;

        @JsonProperty("CustomerId")
        private String customerId;

    }

}
