package fintech.spain.alfa.web.models;

import fintech.spain.alfa.product.registration.forms.AffiliateData;
import fintech.spain.alfa.product.registration.forms.AnalyticsData;
import fintech.spain.alfa.web.validators.LoanTerm;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class SubmitLoanApplicationRequest {

    @NotNull
    private BigDecimal amount;

    @NotNull
    @LoanTerm
    private Long termInDays;

    private String promoCode;

    private AffiliateData affiliate;

    private AnalyticsData analytics;

    public boolean isAffiliate() {
        return affiliate != null && affiliate.getAffiliateName() != null;
    }
}
