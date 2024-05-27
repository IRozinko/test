package fintech.spain.alfa.web.controllers.web;


import fintech.spain.alfa.web.common.WebRequestUtils;
import fintech.spain.alfa.web.models.ContactMeRequest;
import fintech.spain.alfa.web.services.ContactMeService;
import fintech.web.api.models.OkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ContactMeApi {

    @Autowired
    private ContactMeService contactMeService;

    @PostMapping("/api/public/web/contactme")
    public OkResponse sendContactMeRequest(@RequestBody
                                           @Valid
                                           ContactMeRequest contactRequest) {
        contactMeService.sendContactMeRequest(contactRequest, WebRequestUtils.resolveIpAddress());
        return OkResponse.OK;
    }
}
