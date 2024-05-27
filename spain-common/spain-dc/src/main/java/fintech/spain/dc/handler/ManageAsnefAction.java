package fintech.spain.dc.handler;

import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.client.UpdateClientCommand;
import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import fintech.spain.asnef.AsnefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageAsnefAction implements ActionHandler {

    private final ClientService clientService;
    private final AsnefService asnefService;

    @Autowired
    public ManageAsnefAction(ClientService clientService, AsnefService asnefService) {
        this.clientService = clientService;
        this.asnefService = asnefService;
    }

    @Override
    public void handle(ActionContext context) {
        Client client = clientService.get(context.getDebt().getClientId());
        UpdateClientCommand command = UpdateClientCommand.fromClient(client);
        boolean excluded = context.getRequiredParam("exclude", Boolean.class);
        command.setExcludedFromASNEF(excluded);
        clientService.update(command);
        if (excluded) {
            asnefService.makeExhausted(context.getDebt().getLoanId());
        }
    }
}
