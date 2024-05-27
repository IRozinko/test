package fintech.bo.api.server;

import lombok.Data;

@Data
public class Discount {

    private String clientNumber;

    private String rateInPercent;

    private String effectiveFrom;

    private String effectiveTo;

    private String maxTimesToApply;
}
