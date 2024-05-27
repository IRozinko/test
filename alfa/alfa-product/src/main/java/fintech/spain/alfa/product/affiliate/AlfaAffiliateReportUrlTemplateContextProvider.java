package fintech.spain.alfa.product.affiliate;

import fintech.affiliate.model.AffiliateLead;
import fintech.affiliate.spi.AffiliateReportUrlTemplateContextProvider;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.LoanModel;
import fintech.webanalytics.WebAnalyticsService;
import fintech.webanalytics.model.WebAnalyticsEvent;
import fintech.webanalytics.model.WebAnalyticsEventQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AlfaAffiliateReportUrlTemplateContextProvider implements AffiliateReportUrlTemplateContextProvider {

    @Autowired
    private AlfaCmsModels cmsModels;

    @Autowired
    private LoanApplicationService applicationService;

    @Autowired
    private WebAnalyticsService webAnalyticsService;

    @Override
    public Map<String, Object> getContext(AffiliateLead lead) {
        Map<String, Object> context = new HashMap<>();
        context.put("client", cmsModels.client(lead.getClientId()));
        context.put("application", cmsModels.application(lead.getApplicationId()));
        LoanApplication application = applicationService.get(lead.getApplicationId());
        if (application.getLoanId() != null) {
            context.put("loan", cmsModels.loan(application.getLoanId()));
        } else {
            context.put("loan", new LoanModel());
        }
        WebAnalyticsEventQuery query = new WebAnalyticsEventQuery();
        query.setApplicationId(lead.getApplicationId());
        WebAnalyticsEvent event = webAnalyticsService.findLatest(query).orElse(new WebAnalyticsEvent());
        context.put("webEvent", event);
        return context;
    }
}
