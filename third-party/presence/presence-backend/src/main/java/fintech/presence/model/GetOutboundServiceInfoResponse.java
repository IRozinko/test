package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class GetOutboundServiceInfoResponse extends PresenceResponse<GetOutboundServiceInfoResponse.OutboundServiceInfoData> {

    @Data
    @NoArgsConstructor
    public static class ACD {

        @JsonProperty("OutboundVDN")
        private Integer outboundVDN;

        @JsonProperty("OutboundSkill")
        private Integer outboundSkill;

        @JsonProperty("CTILink")
        private String cTILink;

        @JsonProperty("UsePrimaryIfLinkNotConn")
        private Boolean usePrimaryIfLinkNotConn;

        @JsonProperty("EnableMaxConcCalls")
        private Boolean enableMaxConcCalls;

        @JsonProperty("MaxConcCalls")
        private Integer maxConcCalls;

        @JsonProperty("MaxAllowedConcCalls")
        private Integer maxAllowedConcCalls;

        @JsonProperty("PredictiveType")
        private Integer predictiveType;

        @JsonProperty("PredictiveTime")
        private Integer predictiveTime;

        @JsonProperty("PredAutoInterval")
        private Integer predAutoInterval;

        @JsonProperty("ConcurrentCalls")
        private Integer concurrentCalls;

        @JsonProperty("PredAutoAvailTime")
        private Integer predAutoAvailTime;

        @JsonProperty("PredAutoMaxConcCalls")
        private Integer predAutoMaxConcCalls;

        @JsonProperty("EnablePredDefAuto")
        private Boolean enablePredDefAuto;

        @JsonProperty("PredAutoDefContProb")
        private Integer predAutoDefContProb;

        @JsonProperty("PredAutoDefClassTime")
        private Integer predAutoDefClassTime;

        @JsonProperty("PredAutoDefHandleTime")
        private Integer predAutoDefHandleTime;

        @JsonProperty("CheckAgentAvail")
        private Boolean checkAgentAvail;

        @JsonProperty("EnableMinAgentsAvailable")
        private Boolean enableMinAgentsAvailable;

        @JsonProperty("MinAgentsAvailable")
        private String minAgentsAvailable;

        @JsonProperty("PreviewVDN")
        private Integer previewVDN;

        @JsonProperty("EnablePreviewMaxTime")
        private Boolean enablePreviewMaxTime;

        @JsonProperty("PreviewMaxTime")
        private Integer previewMaxTime;

    }

    @Data
    @NoArgsConstructor
    public static class AlternativePhones {

        @JsonProperty("EnableAltPhones")
        private Boolean enableAltPhones;

        @JsonProperty("AltPhonesInUse")
        private Integer altPhonesInUse;

        @JsonProperty("EnableAgentEditAltPhones")
        private Boolean enableAgentEditAltPhones;

        @JsonProperty("EnableAgentAltPhoneStatus")
        private Boolean enableAgentAltPhoneStatus;

        @JsonProperty("EnableDialAllPhones")
        private Boolean enableDialAllPhones;

        @JsonProperty("DialAllPhonesScheduleType")
        private Integer dialAllPhonesScheduleType;

        @JsonProperty("DialAllPhonesSchedPhonePos")
        private Integer dialAllPhonesSchedPhonePos;

        @JsonProperty("DialAllPhonesSchedPhoneDesc")
        private Integer dialAllPhonesSchedPhoneDesc;

        @JsonProperty("DialAllPhonesScheduleInt")
        private Integer dialAllPhonesScheduleInt;

        @JsonProperty("PhoneDescriptions")
        private List<PhoneDescription> phoneDescriptions = null;

    }

    @Data
    @NoArgsConstructor
    public static class CallAnalysis {

        @JsonProperty("DetectAnswerMachine")
        private Boolean detectAnswerMachine;

        @JsonProperty("RingsNoAnswer")
        private Integer ringsNoAnswer;

        @JsonProperty("EnableRedirectOnAMD")
        private Boolean enableRedirectOnAMD;

        @JsonProperty("RedirectOnAMDExtension")
        private Integer redirectOnAMDExtension;

        @JsonProperty("EnableRedirectOnAMDQCode")
        private Boolean enableRedirectOnAMDQCode;

        @JsonProperty("RedirectOnAMDQCode")
        private Integer redirectOnAMDQCode;

    }

    @Data
    @NoArgsConstructor
    public static class CustomFields {

        @JsonProperty("EnableCustomData")
        private Boolean enableCustomData;

        @JsonProperty("ShowCustomData1")
        private Integer showCustomData1;

        @JsonProperty("ShowCustomData2")
        private Integer showCustomData2;

        @JsonProperty("ShowCustomData3")
        private Integer showCustomData3;

        @JsonProperty("CustomDataDesc1")
        private String customDataDesc1;

        @JsonProperty("CustomDataDesc2")
        private String customDataDesc2;

        @JsonProperty("CustomDataDesc3")
        private String customDataDesc3;

    }

    @Data
    @NoArgsConstructor
    public static class DoNotCallLists {

        @JsonProperty("EnableCheckDNCPhones")
        private Boolean enableCheckDNCPhones;

        @JsonProperty("EnableAgentDNCList")
        private Boolean enableAgentDNCList;

        @JsonProperty("AgentDoNotCallListId")
        private Integer agentDoNotCallListId;

        @JsonProperty("DNCListIds")
        private List<Object> dNCListIds = null;

    }

    @Data
    @NoArgsConstructor
    public static class OutboundServiceInfoData {

        @JsonProperty("General")
        private General general;

        @JsonProperty("Integration")
        private Integration integration;

        @JsonProperty("ACD")
        private ACD aCD;

        @JsonProperty("OutboundOptions")
        private OutboundOptions outboundOptions;

        @JsonProperty("CallAnalysis")
        private CallAnalysis callAnalysis;

        @JsonProperty("Maximums")
        private Maximums maximums;

        @JsonProperty("Queues")
        private Queues queues;

        @JsonProperty("Schedule")
        private Schedule schedule;

        @JsonProperty("Softphone")
        private Softphone softphone;

        @JsonProperty("AlternativePhones")
        private AlternativePhones alternativePhones;

        @JsonProperty("TimeZones")
        private TimeZones timeZones;

        @JsonProperty("DoNotCallLists")
        private DoNotCallLists doNotCallLists;

        @JsonProperty("Sounds")
        private Sounds sounds;

        @JsonProperty("CustomFields")
        private CustomFields customFields;

        @JsonProperty("Messaging")
        private Messaging messaging;

        @JsonProperty("Recording")
        private Recording recording;

        @JsonProperty("Other")
        private Other other;

    }

    @Data
    @NoArgsConstructor
    public static class General {

        @JsonProperty("Id")
        private Integer id;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("OutboundType")
        private Integer outboundType;

        @JsonProperty("Status")
        private OutboundServiceStatus status;

        @JsonProperty("NoScheduleGap")
        private Integer noScheduleGap;

        @JsonProperty("EnableSchedLimitDate")
        private Boolean enableSchedLimitDate;

        @JsonProperty("SchedulingLimitDated")
        private String schedulingLimitDated;

        @JsonProperty("ServiceHours")
        private String serviceHours;

        @JsonProperty("StopReasonGroupId")
        private Integer stopReasonGroupId;

        @JsonProperty("ResourceProfileId")
        private Integer resourceProfileId;

        @JsonProperty("ServerId")
        private Integer serverId;

    }

    @Data
    @NoArgsConstructor
    public static class Integration {

        @JsonProperty("IntegrationType")
        private Integer integrationType;

        @JsonProperty("ShowContactInfo")
        private Integer showContactInfo;

        @JsonProperty("QualifContIntegration")
        private Boolean qualifContIntegration;

        @JsonProperty("IntegratedAppFile")
        private String integratedAppFile;

        @JsonProperty("EstablishedURL")
        private String establishedURL;

        @JsonProperty("ClosedURL")
        private String closedURL;

        @JsonProperty("ScriptId")
        private Integer scriptId;

        @JsonProperty("ShowQueuedCallsInfo")
        private Integer showQueuedCallsInfo;

    }

    @Data
    @NoArgsConstructor
    public static class Maximums {

        @JsonProperty("MaxTotal")
        private Integer maxTotal;

        @JsonProperty("MaxDaily")
        private Integer maxDaily;

        @JsonProperty("MaxBusySignal")
        private Integer maxBusySignal;

        @JsonProperty("MaxNoAnswer")
        private Integer maxNoAnswer;

        @JsonProperty("MaxAnswerMachine")
        private Integer maxAnswerMachine;

        @JsonProperty("MaxFax")
        private Integer maxFax;

        @JsonProperty("MaxInvGenReason")
        private Integer maxInvGenReason;

        @JsonProperty("ResetCountersOnSched")
        private Boolean resetCountersOnSched;

    }

    @Data
    @NoArgsConstructor
    public static class Messaging {

        @JsonProperty("EnableFindMail")
        private Boolean enableFindMail;

        @JsonProperty("EnableCompleteMail")
        private Boolean enableCompleteMail;

        @JsonProperty("EnableReplyMail")
        private Boolean enableReplyMail;

        @JsonProperty("EnableSendMail")
        private Boolean enableSendMail;

        @JsonProperty("MailboxId")
        private Integer mailboxId;

    }

    @Data
    @NoArgsConstructor
    public static class Other {

        @JsonProperty("PhonePrefix")
        private String phonePrefix;

        @JsonProperty("MinACWTime")
        private Integer minACWTime;

        @JsonProperty("MaxACWTime")
        private Integer maxACWTime;

        @JsonProperty("MaxACWQCode")
        private Integer maxACWQCode;

        @JsonProperty("UseMaxACWQCodeIfNotQualified")
        private Boolean useMaxACWQCodeIfNotQualified;

        @JsonProperty("EnableCallerId")
        private Boolean enableCallerId;

        @JsonProperty("CallerId")
        private String callerId;

        @JsonProperty("CallerName")
        private String callerName;

        @JsonProperty("EnableCustCallHours")
        private Boolean enableCustCallHours;

    }

    @Data
    @NoArgsConstructor
    public static class OutboundOptions {

        @JsonProperty("EnableCallCapturing")
        private Boolean enableCallCapturing;

        @JsonProperty("PreviewVDN")
        private Integer previewVDN;

        @JsonProperty("EnablePreviewMaxTime")
        private Boolean enablePreviewMaxTime;

        @JsonProperty("PreviewMaxTime")
        private Integer previewMaxTime;

        @JsonProperty("EnablePreviewOnAnswerMachine")
        private Boolean enablePreviewOnAnswerMachine;

        @JsonProperty("PreviewOnAnswerMachineMaxTime")
        private Integer previewOnAnswerMachineMaxTime;

        @JsonProperty("EnablePreviewOnAbandoned")
        private Boolean enablePreviewOnAbandoned;

        @JsonProperty("PreviewOnAbandonedMaxTime")
        private Integer previewOnAbandonedMaxTime;

        @JsonProperty("EnableAbandonedControl")
        private Boolean enableAbandonedControl;

        @JsonProperty("AbandonedControlType")
        private Integer abandonedControlType;

        @JsonProperty("AbandonedRecAdj")
        private Integer abandonedRecAdj;

        @JsonProperty("AbandonedLimit")
        private Integer abandonedLimit;

        @JsonProperty("EnableOutlierControl")
        private Boolean enableOutlierControl;

        @JsonProperty("OutlierSensitivity")
        private Integer outlierSensitivity;

    }

    @Data
    @NoArgsConstructor
    public static class PhoneDescription {

        @JsonProperty("PhoneDescCode")
        private Integer phoneDescCode;

        @JsonProperty("MaxRetries")
        private Integer maxRetries;

        @JsonProperty("EnableCallingHours")
        private Boolean enableCallingHours;

        @JsonProperty("CHoursMonday")
        private String cHoursMonday;

        @JsonProperty("CHoursTuesday")
        private String cHoursTuesday;

        @JsonProperty("CHoursWednesday")
        private String cHoursWednesday;

        @JsonProperty("CHoursThursday")
        private String cHoursThursday;

        @JsonProperty("CHoursFriday")
        private String cHoursFriday;

        @JsonProperty("CHoursSaturday")
        private String cHoursSaturday;

        @JsonProperty("CHoursSunday")
        private String cHoursSunday;

    }

    @Data
    @NoArgsConstructor
    public static class Queues {

        @JsonProperty("ServiceAgents")
        private Integer serviceAgents;

        @JsonProperty("AlternateQueues")
        private Boolean alternateQueues;

        @JsonProperty("AlternateInitial")
        private Integer alternateInitial;

        @JsonProperty("AlternateInvalid")
        private Integer alternateInvalid;

        @JsonProperty("MinBufferInitial")
        private Integer minBufferInitial;

        @JsonProperty("MaxBufferInitial")
        private Integer maxBufferInitial;

        @JsonProperty("MinBufferIGR")
        private Integer minBufferIGR;

        @JsonProperty("MaxBufferIGR")
        private Integer maxBufferIGR;

        @JsonProperty("MinBufferScheduled")
        private Integer minBufferScheduled;

        @JsonProperty("MaxBufferScheduled")
        private Integer maxBufferScheduled;

    }

    @Data
    @NoArgsConstructor
    public static class Recording {

        @JsonProperty("EnableRecOnDemand")
        private Boolean enableRecOnDemand;

        @JsonProperty("ScreenRecording")
        private Boolean screenRecording;

        @JsonProperty("ScreenProfileId")
        private Integer screenProfileId;

        @JsonProperty("EnablePauseRecording")
        private Boolean enablePauseRecording;

    }

    @Data
    @NoArgsConstructor
    public static class Schedule {

        @JsonProperty("BusySignalInt")
        private Integer busySignalInt;

        @JsonProperty("NoAnswerInt")
        private Integer noAnswerInt;

        @JsonProperty("AnswerMachineInt")
        private Integer answerMachineInt;

        @JsonProperty("FaxInt")
        private Integer faxInt;

        @JsonProperty("InvGenReasonInt")
        private Integer invGenReasonInt;

        @JsonProperty("PhoneNotExistInt")
        private Integer phoneNotExistInt;

        @JsonProperty("AbandonedInt")
        private Integer abandonedInt;

        @JsonProperty("ScheduledExpiration")
        private Integer scheduledExpiration;

        @JsonProperty("MaxScheduledRetries")
        private Integer maxScheduledRetries;

        @JsonProperty("DefaultScheduleInt")
        private Integer defaultScheduleInt;

        @JsonProperty("EnableAgentEditScheduledPhone")
        private Boolean enableAgentEditScheduledPhone;

    }

    @Data
    @NoArgsConstructor
    public static class Softphone {

        @JsonProperty("EnableSoftphone")
        private Boolean enableSoftphone;

        @JsonProperty("EnableManualACD")
        private Boolean enableManualACD;

        @JsonProperty("ManualACDLoadId")
        private Integer manualACDLoadId;

        @JsonProperty("EnableInsertOutboundRecord")
        private Boolean enableInsertOutboundRecord;

        @JsonProperty("InsertOutboundRecordLoadId")
        private Integer insertOutboundRecordLoadId;

    }

    @Data
    @NoArgsConstructor
    public static class Sounds {

        @JsonProperty("EnableAnswerSound")
        private Boolean enableAnswerSound;

    }

    @Data
    @NoArgsConstructor
    public static class TimeZones {

        @JsonProperty("EnableTimeZone")
        private Boolean enableTimeZone;

        @JsonProperty("EnableNPATZ")
        private Boolean enableNPATZ;

        @JsonProperty("EnableCheckNPATZ")
        private Boolean enableCheckNPATZ;

        @JsonProperty("TimeZoneId")
        private String timeZoneId;

    }

}
