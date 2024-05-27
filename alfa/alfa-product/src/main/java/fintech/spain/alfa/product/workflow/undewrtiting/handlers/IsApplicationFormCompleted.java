package fintech.spain.alfa.product.workflow.undewrtiting.handlers;


import fintech.crm.address.ClientAddress;
import fintech.crm.address.ClientAddressService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.AutoCompletePrecondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class IsApplicationFormCompleted implements AutoCompletePrecondition {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientAddressService clientAddressService;

    @Override
    public boolean isTrueFor(ActivityContext context) {
        Long clientId = context.getWorkflow().getClientId();
        Client client = clientService.get(clientId);
        Optional<ClientAddress> maybeClientAddress = clientAddressService.getClientPrimaryAddress(clientId, AlfaConstants.ADDRESS_TYPE_ACTUAL);

        return client.getDateOfBirth() != null
            && client.getGender() != null
            && client.getDocumentNumber() != null
            && maybeClientAddress.isPresent();
    }
}
