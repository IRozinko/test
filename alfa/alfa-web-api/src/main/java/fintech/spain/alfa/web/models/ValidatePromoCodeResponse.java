package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ValidatePromoCodeResponse {

    private String promoCode;

    private BigDecimal discount;

}
