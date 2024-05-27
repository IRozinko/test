package fintech.spain.alfa.product.affiliate;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AffiliateExperianResult {
    boolean verified;
    Long caisResumentResponseId;
    Long caisOperacionesResponseId;
}
