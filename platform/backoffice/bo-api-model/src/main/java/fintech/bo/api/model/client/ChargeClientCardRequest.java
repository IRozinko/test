package fintech.bo.api.model.client;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ChargeClientCardRequest {

    @NotNull
    private Long clientId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Long paymentCardId;
    @NotNull
    private String details;

}
