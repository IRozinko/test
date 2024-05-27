package fintech.affiliate.model;

import lombok.Data;

@Data
public class AffiliateLead {
    private Long id;
    private Long partnerId;
    private String partnerName;
    private Long clientId;
    private Long applicationId;
    private boolean unknownPartner;
    private String affiliateName;
    private String campaign;
    private String affiliateLeadId;
    private String subAffiliateLeadId1;
    private String subAffiliateLeadId2;
    private String subAffiliateLeadId3;
    private boolean repeatedClient;
}
