package fintech.affiliate.model;

import lombok.Data;

@Data
public class AddLeadCommand {

    private Long clientId;
    private Long applicationId;
    private String affiliateName;
    private String campaign;
    private String affiliateLeadId;
    private String subAffiliateLeadId1;
    private String subAffiliateLeadId2;
    private String subAffiliateLeadId3;
    private boolean repeatedClient;
}
