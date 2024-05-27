package fintech.bo.api.server;

import fintech.affiliate.AffiliateService;
import fintech.affiliate.model.SavePartnerCommand;
import fintech.bo.api.model.affiliate.SavePartnerRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AffiliateApiController {

    private final AffiliateService service;

    @Autowired
    public AffiliateApiController(AffiliateService service) {
        this.service = service;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.AFFILIATE_EDIT})
    @PostMapping(path = "/api/bo/affiliate/save-partner")
    public void savePartner(@RequestBody SavePartnerRequest request) {
        SavePartnerCommand command = new SavePartnerCommand();
        command.setName(request.getName());
        command.setActive(request.isActive());
        command.setLeadReportUrl(request.getLeadReportUrl());
        command.setRepeatedClientLeadReportUrl(request.getRepeatedClientLeadReportUrl());
        command.setActionReportUrl(request.getActionReportUrl());
        command.setRepeatedClientActionReportUrl(request.getRepeatedClientActionReportUrl());
        command.setLeadConditionWorkflowActivityName(request.getLeadConditionWorkflowActivityName());
        command.setLeadConditionWorkflowActivityResolution(request.getLeadConditionWorkflowActivityResolution());
        command.setApiKey(request.getApiKey());
        service.savePartner(command);
    }

}
