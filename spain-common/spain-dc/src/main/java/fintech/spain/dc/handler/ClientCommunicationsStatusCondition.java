package fintech.spain.dc.handler;

import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientCommunicationsStatusCondition implements ConditionHandler {

    private final ClientService clientService;

    @Autowired
    public ClientCommunicationsStatusCondition(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public boolean apply(ConditionContext context) {
        boolean blocked = context.getParam("blocked", Boolean.class).orElse(false);
        Client client = clientService.get(context.getDebt().getClientId());
        return blocked == client.isBlockCommunication();
    }
}
