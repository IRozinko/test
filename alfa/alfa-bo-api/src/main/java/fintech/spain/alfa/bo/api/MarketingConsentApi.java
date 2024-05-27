package fintech.spain.alfa.bo.api;

import fintech.bo.api.model.marketing.ChangeMarketingConsentRequest;
import fintech.crm.client.ClientService;
import fintech.crm.client.model.ChangeAcceptMarketingCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarketingConsentApi {

    private final ClientService clientService;

    @Autowired
    public MarketingConsentApi(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/api/bo/marketing/update")
    public ResponseEntity updateMarketingConsent(@RequestBody ChangeMarketingConsentRequest req) {
        clientService.updateAcceptMarketing(ChangeAcceptMarketingCommand.builder()
            .clientId(req.getClientId())
            .source(req.getSource())
            .note(req.getNote())
            .emailActivityId(req.getEmailActivityId())
            .newValue(req.getNewValue())
            .build());

        return ResponseEntity.noContent().build();
    }


}
