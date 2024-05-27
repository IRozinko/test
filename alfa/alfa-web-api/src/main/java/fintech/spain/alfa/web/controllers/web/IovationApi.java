package fintech.spain.alfa.web.controllers.web;

import fintech.iovation.IovationService;
import fintech.iovation.model.SaveBlackboxCommand;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatus;
import fintech.spain.alfa.web.common.WebRequestUtils;
import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.web.models.SaveIovationBlackboxRequest;
import fintech.spain.alfa.product.web.WebAuthorities;
import fintech.web.api.models.OkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class IovationApi {

    @Autowired
    private IovationService iovationService;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Secured(WebAuthorities.WEB_FULL)
    @PostMapping("/api/web/iovation/save-blackbox")
    public OkResponse saveBlackbox(@AuthenticationPrincipal WebApiUser user, @RequestBody @Valid SaveIovationBlackboxRequest request) {
        String ipAddress = WebRequestUtils.resolveIpAddress();

        SaveBlackboxCommand command = new SaveBlackboxCommand();
        command.setBlackBox(request.getBlackBox());
        command.setClientId(user.getClientId());
        command.setIpAddress(ipAddress);

        loanApplicationService.findLatest(LoanApplicationQuery.byClientId(user.getClientId(), LoanApplicationStatus.OPEN))
            .ifPresent(la -> command.setLoanApplicationId(la.getId()));

        iovationService.saveBlackbox(command);
        return OkResponse.OK;
    }

}
