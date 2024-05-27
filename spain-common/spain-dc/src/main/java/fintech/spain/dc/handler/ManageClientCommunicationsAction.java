package fintech.spain.dc.handler;

import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.client.UpdateClientCommand;
import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageClientCommunicationsAction implements ActionHandler {

    private final ClientService clientService;

    @Autowired
    public ManageClientCommunicationsAction(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void handle(ActionContext context) {
        Client client = clientService.get(context.getDebt().getClientId());
        UpdateClientCommand command = UpdateClientCommand.fromClient(client);
        boolean block = context.getRequiredParam("block", Boolean.class);
        command.setBlockCommunication(block);
        clientService.update(command);
    }
}
