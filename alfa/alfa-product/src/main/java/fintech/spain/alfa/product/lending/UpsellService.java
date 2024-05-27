package fintech.spain.alfa.product.lending;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Validated
public interface UpsellService {

    Long issueUpsell(@NotNull Long loanApplicationId, @NotNull LocalDate issueDate);

    Long disburseUpsell(@NotNull Long disbursementId);
}
