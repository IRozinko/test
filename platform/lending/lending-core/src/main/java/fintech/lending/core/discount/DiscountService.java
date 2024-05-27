package fintech.lending.core.discount;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;

@Validated
public interface DiscountService {

    Discount applyDiscount(@NotNull @Valid ApplyDiscountCommand command);

    Optional<Discount> findDiscount(@NotNull Long clientId, @NotNull LocalDate when);

    Discount get(@NotNull Long discountId);
}
