package fintech.spain.dc.handler;

import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.client.UpdateClientCommand;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.spain.asnef.AsnefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageAsnefBulkAction implements BulkActionHandler {

    private final ClientService clientService;
    private final AsnefService asnefService;

    @Autowired
    public ManageAsnefBulkAction(ClientService clientService, AsnefService asnefService) {
        this.clientService = clientService;
        this.asnefService = asnefService;
    }

    @Override
    public void handle(BulkActionContext context) {
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
