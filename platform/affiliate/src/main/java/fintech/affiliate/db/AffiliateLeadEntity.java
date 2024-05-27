package fintech.affiliate.db;


import fintech.affiliate.model.AffiliateLead;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "lead", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_lead_client_id"),
    @Index(columnList = "partner_id", name = "idx_lead_partner_id"),
})
@DynamicUpdate
public class AffiliateLeadEntity extends BaseEntity {

    @ManyToOne(optional = true)
    @JoinColumn(name = "partner_id", nullable = true)
    private AffiliatePartnerEntity partner;

    @Column(nullable = false)
    private Long clientId;
    private Long applicationId;

    @Column(nullable = false)
    private boolean unknownPartner;
    private String affiliateName;
    private String campaign;
    private String affiliateLeadId;
    private String subAffiliateLeadId1;
    private String subAffiliateLeadId2;
    private String subAffiliateLeadId3;

    @Column(nullable = false)
    private boolean repeatedClient;

    public AffiliateLead toValueObject() {
        AffiliateLead vo = new AffiliateLead();
        vo.setId(this.id);
        vo.setPartnerId(this.partner.getId());
        vo.setPartnerName(this.partner.getName());
        vo.setClientId(this.clientId);
        vo.setApplicationId(this.applicationId);
        vo.setUnknownPartner(this.unknownPartner);
        vo.setAffiliateName(this.affiliateName);
        vo.setCampaign(this.campaign);
        vo.setAffiliateLeadId(this.affiliateLeadId);
        vo.setSubAffiliateLeadId1(this.subAffiliateLeadId1);
        vo.setSubAffiliateLeadId2(this.subAffiliateLeadId2);
        vo.setSubAffiliateLeadId3(this.subAffiliateLeadId3);
        vo.setRepeatedClient(repeatedClient);
        return vo;
    }

}
