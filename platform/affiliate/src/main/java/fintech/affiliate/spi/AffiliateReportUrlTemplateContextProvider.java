package fintech.affiliate.spi;

import fintech.affiliate.model.AffiliateLead;

import java.util.Map;

public interface AffiliateReportUrlTemplateContextProvider {

    Map<String, Object> getContext(AffiliateLead lead);
}
