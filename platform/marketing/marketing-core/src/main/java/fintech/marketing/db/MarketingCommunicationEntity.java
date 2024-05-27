package fintech.marketing.db;


import fintech.db.BaseEntity;
import fintech.marketing.MarketingCommunicationStatus;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "marketing_communication", schema = Entities.SCHEMA)
public class MarketingCommunicationEntity extends BaseEntity {

    @Column(nullable = false)
    private Long marketingCampaignId;

    @Column(nullable = false)
    private String emailBody;

    @Column(nullable = false)
    private String emailSubject;

    private String sms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketingCommunicationStatus status;

    @Column(nullable = false, name = "audience_settings_json_config")
    private String audienceSettingsJson;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime nextActionAt;

    private Long promoCodeId;

    @Column(nullable = false)
    private boolean reminder;

    private Integer targetedUsers;

    private BigDecimal viewRate;

    private BigDecimal clickRate;

    private Long imageFileId;

    private String lastExecutionResult;

    private String viewsHllHex;
    private String clicksHllHex;

}
