package fintech.marketing.db;


import fintech.db.BaseEntity;
import fintech.marketing.MarketingCampaignScheduleType;
import fintech.marketing.MarketingCampaignStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "marketing_campaign", schema = Entities.SCHEMA)
public class MarketingCampaignEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String emailBody;

    @Column(nullable = false)
    private String emailSubject;

    private String sms;

    private String remindEmailBody;
    private String remindEmailSubject;

    @Enumerated(EnumType.STRING)
    private MarketingCampaignScheduleType scheduleType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MarketingCampaignStatus status = MarketingCampaignStatus.ACTIVE;

    @Column(nullable = false)
    private Integer remindIntervalHours;

    @Column(nullable = false, name = "audience_settings_json_config")
    private String audienceSettingsJson;

    @Column(nullable = false)
    private Long mainMarketingTemplateId;

    private Long remindMarketingTemplateId;

    private Long mainPromoCodeId;
    private Long remindPromoCodeId;

    @Column(nullable = false)
    private Long mainImageFileId;
    private Long remindImageFileId;

    @Column(nullable = false)
    private boolean hasMainPromoCode;

    @Column(nullable = false)
    private boolean hasRemindPromoCode;

    @Column(nullable = false)
    private boolean enableRemind;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime scheduleDate;

}
