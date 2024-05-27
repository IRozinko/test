package fintech.spain.alfa.product.referral;

import lombok.Data;

import java.util.List;

@Data
public class ReferralLendingCompanySettings {
    private String name;
    private String link;
    private List<String> excludeTraffic;
}
