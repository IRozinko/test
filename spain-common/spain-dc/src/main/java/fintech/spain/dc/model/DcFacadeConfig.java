package fintech.spain.dc.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DcFacadeConfig {

    private String reExternalizedPortfolio;
    private int minDpdForSell = Integer.MIN_VALUE;
    private int minDpdForExternalize = Integer.MIN_VALUE;


}
