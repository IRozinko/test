package fintech.spain.alfa.product.affiliate;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AffiliateApplicationInfo {

    private AffiliateRegistrationStep1Form form;
    private String affiliateName;
    private Long clientId;
    private Long equifaxResponseId;
    private Long experianCaisResumentResponseId;
    private Long experianCaisOperacionesResponseId;

}
