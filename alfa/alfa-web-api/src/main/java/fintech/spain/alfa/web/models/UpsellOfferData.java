package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class UpsellOfferData {

    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal total;

    // TAE
    private BigDecimal aprPercent;
    // TIN mensual
    private BigDecimal monthlyInterestRatePercent;
    private BigDecimal nominalApr;
}
