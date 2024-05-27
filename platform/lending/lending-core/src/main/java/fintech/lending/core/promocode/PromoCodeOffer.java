package fintech.lending.core.promocode;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class PromoCodeOffer {

    private Long promoCodeId;
    private String promoCode;
    private BigDecimal discountInPercent;

}
