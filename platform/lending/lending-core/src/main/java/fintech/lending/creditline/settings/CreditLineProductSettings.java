package fintech.lending.creditline.settings;

import fintech.lending.core.product.ProductSettings;
import fintech.lending.creditline.validators.ValidInterestsRate;
import lombok.Data;

@Data
public class CreditLineProductSettings implements ProductSettings {

    private CreditLineOfferSettings offerSettings;

    @ValidInterestsRate
    private CreditLinePricingSettings pricingSettings;

    private CreditLineInvoiceSettings invoiceSettings;

    private CreditLinePenaltySettings penaltySettings;

    private CreditLineRepaymentSettings repaymentSettings;

    private CreditLineWithdrawalSettings withdrawalSettings;

}
