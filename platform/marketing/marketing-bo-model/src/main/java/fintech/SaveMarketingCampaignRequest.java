package fintech.bo.api.model.marketing;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class SaveMarketingCampaignRequest {

    public static final String WEEKLY = "WEEKLY";
    public static final String DAILY = "DAILY";
    public static final String MONTHLY = "MONTHLY";

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String emailBody;

    @NotNull
    private String emailSubject;
    private String sms;

    private String remindEmailBody;
    private String remindEmailSubject;

    private String scheduleType;

    @NotNull
    private Boolean automated;

    private Integer remindIntervalHours;

    private LocalDateTime triggerDate;

    @NotNull
    private Boolean triggerNow;

    @NotNull
    private Boolean enableRemind;

    @NotNull
    private String audienceSettingsJson;

    private Long mainPromoCodeId;
    private Long remindPromoCodeId;

    @NotNull
    private Boolean hasMainPromoCodeId;

    @NotNull
    private Boolean hasRemindPromoCodeId;

    @NotNull
    private Long mainMarketingTemplateId;

    private Long remindMarketingTemplateId;

    @AssertTrue
    public boolean isTriggeringValid() {
        return triggerDate != null || triggerNow;
    }

    @AssertTrue
    public boolean isRemindPromoCodeValid() {
        return !hasRemindPromoCodeId || remindPromoCodeId != null;
    }

    @AssertTrue
    public boolean isRemindValid() {
        return !enableRemind || (remindMarketingTemplateId != null && remindIntervalHours != null && remindEmailBody != null && remindEmailSubject != null);
    }
}
