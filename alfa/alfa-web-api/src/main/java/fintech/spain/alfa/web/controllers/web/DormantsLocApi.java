package fintech.spain.alfa.web.controllers.web;

import fintech.crm.client.model.DormantsCompleteWFRequest;
import fintech.spain.alfa.web.config.security.CurrentClient;
import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.web.models.DormantsLocOffer;
import fintech.spain.alfa.web.services.WebDormantsLocService;
import fintech.spain.alfa.product.lending.LineOfCreditFacade;
import fintech.web.api.models.OkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DormantsLocApi {

    @Autowired
    private WebDormantsLocService webDormantsLocService;

    @Autowired
    private LineOfCreditFacade lineOfCreditFacade;

    @GetMapping("/api/web/wf/dormants-loc/offer")
    public DormantsLocOffer getOffer(@CurrentClient WebApiUser user) {
        return webDormantsLocService.getOffer(user.getClientId());
    }

    @PostMapping("/api/internal/wf/dormants-loc/complete")
    public OkResponse completeWorkflow(@RequestBody DormantsCompleteWFRequest request) {
        log.info("Received callback for dormant client [{}]", request);
        lineOfCreditFacade.completeWorkflow(request.getClientId());
        lineOfCreditFacade.markClientAsTransferred(request.getClientId());
        return OkResponse.OK;
    }
}
