package fintech.spain.dc.handler;

import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AsnefStatusCondition implements ConditionHandler {

    private final ClientService clientService;

    @Autowired
    public AsnefStatusCondition(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public boolean apply(ConditionContext context) {
        boolean excluded = context.getParam("excluded", Boolean.class).orElse(false);
        Client client = clientService.get(context.getDebt().getClientId());
        return excluded == client.isExcludedFromASNEF();
    }
}
