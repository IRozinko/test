package fintech.spain.alfa.bo.api;

import fintech.bo.api.model.client.InitiateChangingBankAccountRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.spain.alfa.product.crm.spi.ClientConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class ClientConfigurationApiController {

    private final ClientConfigurationService clientConfigurationService;

    public ClientConfigurationApiController(ClientConfigurationService clientConfigurationService) {
        this.clientConfigurationService = clientConfigurationService;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_INITIATE_CHANGING_BANK_ACCOUNT})
    @PostMapping("api/bo/client/initiate-changing-bank-account")
    public void initiateChangingBankAccount(@Valid @RequestBody InitiateChangingBankAccountRequest request) {
        clientConfigurationService.initiateChangingBankAccount(request.getClientId());
    }

}
