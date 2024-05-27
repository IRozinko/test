package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class PrepareOfferRequest {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long termInDays;

    private String promoCode;

}
