package fintech.webanalytics.db;

import fintech.db.BaseEntity;
import fintech.webanalytics.model.WebAnalyticsEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "event", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_event_client_id"),
    @Index(columnList = "createdAt", name = "idx_event_created_at"),
})
public class WebAnalyticsEventEntity extends BaseEntity {

    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private String ipAddress;

    @Column(nullable = false)
    private String eventType;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmTerm;
    private String utmContent;
    private String gclid;

    public WebAnalyticsEvent toValueObject() {
        WebAnalyticsEvent vo = new WebAnalyticsEvent();
        vo.setId(this.id);
        vo.setClientId(this.clientId);
        vo.setApplicationId(this.applicationId);
        vo.setLoanId(this.loanId);
        vo.setIpAddress(this.ipAddress);
        vo.setEventType(this.eventType);
        vo.setUtmSource(this.utmSource);
        vo.setUtmMedium(this.utmMedium);
        vo.setUtmCampaign(this.utmCampaign);
        vo.setUtmTerm(this.utmTerm);
        vo.setUtmContent(this.utmContent);
        vo.setGclid(this.getGclid());
        return vo;
    }
}
