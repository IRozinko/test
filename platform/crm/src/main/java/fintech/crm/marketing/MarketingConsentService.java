package fintech.crm.marketing;

import fintech.crm.marketing.event.ClientMarketingConsentChanged;

public interface MarketingConsentService {

    void handleMarketingConsentChanged(ClientMarketingConsentChanged event);
}
