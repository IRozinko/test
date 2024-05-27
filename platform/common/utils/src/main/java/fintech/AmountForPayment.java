package fintech;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountForPayment {
    @NonNull
    private BigDecimal roundedAmount;

    @NonNull
    private BigDecimal roundingDifferenceAmount;
}
