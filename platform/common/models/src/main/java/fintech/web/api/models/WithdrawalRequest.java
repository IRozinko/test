package fintech.web.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WithdrawalRequest {

    @NotNull
    private BigDecimal amount;

}
