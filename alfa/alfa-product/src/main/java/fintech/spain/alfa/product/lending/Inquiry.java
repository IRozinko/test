package fintech.spain.alfa.product.lending;

import fintech.lending.core.application.LoanApplicationSourceType;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class Inquiry {

    private Long interestStrategyId;

    @NotNull
    @Min(0)
    private BigDecimal principal;

    @NotNull
    @Min(1)
    private Long termInDays;

    @NotNull
    @Min(0)
    @Max(100)
    private BigDecimal interestDiscountPercent;

    @NotNull
    private LocalDateTime submittedAt;

    private LoanApplicationSourceType sourceType = LoanApplicationSourceType.ORGANIC;

    private String sourceName;

    private Long discountId;

    private Long promoCodeId;

    private Long applicationId;
}
