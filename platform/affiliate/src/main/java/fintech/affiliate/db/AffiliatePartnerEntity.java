package fintech.affiliate.db;


import fintech.affiliate.model.AffiliatePartner;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "partner", schema = Entities.SCHEMA)
@DynamicUpdate
public class AffiliatePartnerEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean active;

    private String leadReportUrl;
    private String repeatedClientLeadReportUrl;

    private String actionReportUrl;
    private String repeatedClientActionReportUrl;

    private String leadConditionWorkflowActivityName;
    private String leadConditionWorkflowActivityResolution;

    private String apiKey;

    public AffiliatePartner toValueObject() {
        AffiliatePartner vo = new AffiliatePartner();
        vo.setName(this.name);
        vo.setActive(this.active);
        return vo;
    }
}
