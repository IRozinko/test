package fintech.marketing;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class SaveMarketingCampaignCommand {

    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String emailBody;

    @NotEmpty
    private String emailSubject;
    private String sms;

    private String remindEmailBody;
    private String remindEmailSubject;

    private MarketingCampaignScheduleType scheduleType;
    private Integer remindIntervalHours;

    @NotNull
    private LocalDateTime triggerDate;

    @NotNull
    private String audienceSettingsJson;

    private Long mainPromoCodeId;
    private Long remindPromoCodeId;

    @NotNull
    private byte[] mainCampaignImage;
    private byte[] remindCampaignImage;

    private boolean hasMainPromoCodeId;
    private boolean hasRemindPromoCodeId;

    private Long mainMarketingTemplateId;
    private Long remindMarketingTemplateId;

    private boolean enableRemind;
}
