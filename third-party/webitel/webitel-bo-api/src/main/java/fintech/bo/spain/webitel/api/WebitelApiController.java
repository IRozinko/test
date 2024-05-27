package fintech.bo.spain.webitel.api;


import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.webitel.WebitelService;
import fintech.webitel.model.WebitelAuthToken;
import fintech.webitel.model.WebitelCallResult;
import fintech.webitel.model.WebitelLoginCommand;
import fintech.webitel.model.WebitelCallCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebitelApiController {

    private final WebitelService webitelService;

    @Autowired
    public WebitelApiController(WebitelService webitelService) {
        this.webitelService = webitelService;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_CALL_WEBITEL})
    @PostMapping("api/bo/webitel/login")
    public WebitelAuthToken login(@RequestBody WebitelLoginCommand request) {
        return webitelService.authenticate(request);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_CALL_WEBITEL})
    @PostMapping("api/bo/webitel/call")
    public WebitelCallResult call(@RequestBody WebitelCallCommand request) {
        return webitelService.originateNewCall(request);
    }
}
