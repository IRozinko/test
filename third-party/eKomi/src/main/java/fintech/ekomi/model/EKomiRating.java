package fintech.ekomi.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ToString
public class EKomiRating {

    private Long count;

    private BigDecimal average;

}
