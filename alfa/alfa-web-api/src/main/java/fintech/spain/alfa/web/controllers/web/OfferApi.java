package fintech.spain.alfa.web.controllers.web;

import fintech.lending.core.application.LoanApplication;
import fintech.spain.alfa.web.common.WebRequestUtils;
import fintech.spain.alfa.web.models.LoginResponse;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import fintech.spain.alfa.product.web.WebLoginService;
import fintech.spain.web.common.ValidationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class OfferApi {

    @Autowired
    private UnderwritingFacade underwritingFacade;

    @Autowired
    private WebLoginService webLoginService;

    @Autowired
    private ValidationExceptions validationExceptions;

    @GetMapping("/api/public/web/approve-offer")
    public LoginResponse approveOffer(@RequestParam("code") String longCode) {
        LoanApplication application = underwritingFacade.approveApplicationWithLongCode(longCode, WebRequestUtils.resolveIpAddress());
        if (application != null) {
            String token = webLoginService.login(application.getClientId(), Duration.ofHours(2));
            return new LoginResponse(token);
        } else {
            throw validationExceptions.loanAlreadyAccepted("code");
        }
    }

}
