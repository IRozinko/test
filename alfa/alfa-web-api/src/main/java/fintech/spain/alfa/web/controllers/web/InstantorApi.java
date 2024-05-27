package fintech.spain.alfa.web.controllers.web;

import fintech.spain.alfa.web.config.security.CurrentClient;
import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.product.web.WebAuthorities;
import fintech.spain.alfa.product.workflow.dormants.event.InstantorCanceledByClient;
import fintech.web.api.models.OkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstantorApi {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Secured(WebAuthorities.WEB_FULL)
    @PostMapping("/api/web/wf/cancel-instantor")
    public OkResponse cancelInstantor(@CurrentClient WebApiUser client) {
        eventPublisher.publishEvent(new InstantorCanceledByClient(client.getClientId()));
        return OkResponse.OK;
    }


}
