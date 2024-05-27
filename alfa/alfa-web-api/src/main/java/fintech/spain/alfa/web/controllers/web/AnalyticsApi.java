package fintech.spain.alfa.web.controllers.web;

import fintech.Validate;
import fintech.db.AuditInfoProvider;
import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.product.web.WebAuthorities;
import fintech.webanalytics.WebAnalyticsService;
import fintech.webanalytics.model.SaveEventCommand;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsApi {

    @Autowired
    private WebAnalyticsService webAnalyticsService;

    @Autowired
    private AuditInfoProvider auditInfoProvider;

    @Secured(WebAuthorities.WEB_FULL)
    @PostMapping("/api/public/web/analytics")
    public void saveEvent(@RequestBody AnalyticsEventRequest request, @AuthenticationPrincipal WebApiUser user) {
        Validate.notNull(request.getEventType(), "'eventType' must be provided");

        SaveEventCommand command = new SaveEventCommand();
        command.setClientId(user.getClientId());
        command.setApplicationId(request.getLoanApplicationId());
        command.setLoanId(request.getLoanId());
        command.setIpAddress(auditInfoProvider.getInfo().getIpAddress());
        command.setEventType(request.getEventType());
        command.setUtmSource(request.getUtmSource());
        command.setUtmMedium(request.getUtmMedium());
        command.setUtmCampaign(request.getUtmCampaign());
        command.setUtmTerm(request.getUtmTerm());
        command.setUtmContent(request.getUtmContent());
        webAnalyticsService.saveEvent(command);
    }

    @Data
    public static class AnalyticsEventRequest {
        private String eventType;
        private Long loanApplicationId;
        private Long loanId;
        private String utmSource;
        private String utmMedium;
        private String utmCampaign;
        private String utmTerm;
        private String utmContent;
    }

}
