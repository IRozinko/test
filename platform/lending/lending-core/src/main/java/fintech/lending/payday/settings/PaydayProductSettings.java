package fintech.lending.payday.settings;

import fintech.lending.core.product.ProductSettings;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PaydayProductSettings implements ProductSettings {

    @NotNull
    private PaydayOfferSettings publicOfferSettings;

    @NotNull
    private PaydayOfferSettings clientOfferSettings;
}
