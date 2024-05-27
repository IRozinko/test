package fintech.spain.alfa.product.cms;

import fintech.spain.platform.web.model.SpecialLink;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DebtModel {

    private BigDecimal totalDue;
    private SpecialLink specialLink;
}
