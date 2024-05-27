package fintech.spain.alfa.product.extension;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ExtensionInquiry {

    private Long productId;
    private BigDecimal principal = amount(0);

}
