package fintech.spain.alfa.product.registration.forms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AffiliateData {

    private String affiliateName;
    private String campaign;
    private String affiliateLeadId;
    private String subAffiliateLeadId1;
    private String subAffiliateLeadId2;
    private String subAffiliateLeadId3;

}
