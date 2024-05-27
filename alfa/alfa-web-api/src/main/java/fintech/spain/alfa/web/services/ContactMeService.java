package fintech.spain.alfa.web.services;

import fintech.spain.alfa.web.models.ContactMeRequest;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.cms.ContactMeModel;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ContactMeService {

    @Autowired
    private AlfaNotificationBuilderFactory alfaNotificationBuilderFactory;
    @Autowired
    private AlfaCmsModels cmsModels;

    public void sendContactMeRequest(ContactMeRequest contactMeRequest, String ipAddress) {
        alfaNotificationBuilderFactory.contactMe()
            .emailFromName(contactMeRequest.getName())
            .emailFrom(contactMeRequest.getEmail())
            .renderAnonymous(CmsSetup.CONTACT_ME, getEmailProperties(contactMeRequest, ipAddress))
            .send();
    }

    private Map<String, Object> getEmailProperties(ContactMeRequest contactMeRequest, String ipAddress) {
        return cmsModels.contactMe(
            new ContactMeModel()
                .setComment(contactMeRequest.getComment())
                .setName(contactMeRequest.getName())
                .setIpAddress(ipAddress)
                .setPhone(contactMeRequest.getPhone())
                .setEmail(contactMeRequest.getEmail())
        );
    }
}
