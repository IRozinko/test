package fintech.lending.revolving.settings;

import fintech.lending.core.product.ProductSettings;
import lombok.Data;

@Data
public class RevolvingProductSettings implements ProductSettings {

    private RevolvingPricingSettings pricingSettings;
    private RevolvingInvoiceSettings invoiceSettings;
    private RevolvingRepaymentSettings repaymentSettings;
    private RevolvingWithdrawalSettings withdrawalSettings;
    private RevolvingPenaltySettings penaltySettings;

}
