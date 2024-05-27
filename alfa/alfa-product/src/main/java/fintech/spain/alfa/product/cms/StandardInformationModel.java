package fintech.spain.alfa.product.cms;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StandardInformationModel {

    private BigDecimal effectiveApr;

    private BigDecimal interestPercentageRatePerDay;

    private BigDecimal penaltyPercentageRatePerDay;

    private BigDecimal maxProductAmount;

    private BigDecimal examplePrincipalAmount;

    private BigDecimal exampleRepaymentAmount;

    private Integer maxProductPeriodCount;

}
