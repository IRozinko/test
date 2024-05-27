package fintech.spain.alfa.product.cms;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class UpsellModel {

    private ApplicationModel upsell;
    private ApplicationModel loan;

    private String loanNumber;

    private BigDecimal totalPrincipal;
    private BigDecimal totalInterest;
    private BigDecimal grandTotal;
    private BigDecimal averageNominalApr;
    private BigDecimal averageEffectiveApr;
}
