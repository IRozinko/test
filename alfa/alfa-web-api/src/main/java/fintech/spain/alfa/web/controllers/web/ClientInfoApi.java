package fintech.spain.alfa.web.controllers.web;


import fintech.spain.alfa.web.config.security.CurrentClient;
import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.web.models.ClientInfoResponse;
import fintech.spain.alfa.web.services.ClientInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientInfoApi {

    @Autowired
    private ClientInfoService clientInfoService;

    @GetMapping("/api/public/web/client")
    public ClientInfoResponse getClientInfo(@CurrentClient WebApiUser user) {
        if (user == null) {
            return ClientInfoResponse.notAuthenticated();
        } else {
            ClientInfoResponse info = clientInfoService.get(user.getClientId());
            info.setRoles(user.getRoles());
            return info;
        }
    }
}
