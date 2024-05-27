package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ApproveUpsellOfferRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal principal;

    @Pattern(regexp = "CONTROL|VARIANT")
    private String absource;
}
