package fintech.spain.dc.handler;

import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.client.UpdateClientCommand;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageClientCommunicationsBulkAction implements BulkActionHandler {

    private final ClientService clientService;

    @Autowired
    public ManageClientCommunicationsBulkAction(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void handle(BulkActionContext context) {
        Client client = clientService.get(context.getDebt().getClientId());
        UpdateClientCommand command = UpdateClientCommand.fromClient(client);
        boolean block = context.getRequiredParam("block", Boolean.class);
        command.setBlockCommunication(block);
        clientService.update(command);
    }
}
