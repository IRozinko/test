package fintech.affiliate.db;


import fintech.affiliate.model.EventType;
import fintech.affiliate.model.ReportStatus;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "event", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_event_client_id"),
    @Index(columnList = "partner_id", name = "idx_event_partner_id"),
    @Index(columnList = "lead_id", name = "idx_event_lead_id"),
    @Index(columnList = "nextReportAttemptAt", name = "idx_event_next_report_attempt_at"),
})
@DynamicUpdate
public class AffiliateEventEntity extends BaseEntity {

    @ManyToOne(optional = true)
    @JoinColumn(name = "partner_id", nullable = true)
    private AffiliatePartnerEntity partner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lead_id")
    private AffiliateLeadEntity lead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus reportStatus;

    @Column(nullable = false)
    private Long clientId;
    private Long applicationId;
    private Long loanId;

    @Column(nullable = false)
    private int reportRetryAttempts;
    private LocalDateTime nextReportAttemptAt;
    private LocalDateTime reportedAt;
    private String reportUrl;
    private String reportError;
}
