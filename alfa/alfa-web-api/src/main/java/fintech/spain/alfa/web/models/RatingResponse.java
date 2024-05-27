package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class RatingResponse {

    private Long count = 0l;
    private BigDecimal average = BigDecimal.ZERO;
}
