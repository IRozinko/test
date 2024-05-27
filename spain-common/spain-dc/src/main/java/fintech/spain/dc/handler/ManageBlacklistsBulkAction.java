package fintech.spain.dc.handler;

import fintech.crm.CrmConstants;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.spain.crm.client.ClientFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageBlacklistsBulkAction implements BulkActionHandler {

    private final ClientFacade clientFacade;

    @Autowired
    public ManageBlacklistsBulkAction(ClientFacade clientFacade) {
        this.clientFacade = clientFacade;
    }

    @Override
    public void handle(BulkActionContext context) {
        boolean document = context.getParam("document", Boolean.class).orElse(false);
        boolean email = context.getParam("email", Boolean.class).orElse(false);
        boolean phone = context.getParam("phone", Boolean.class).orElse(false);

        Long clientId = context.getDebt().getClientId();
        String comment = context.getCommand().getActionName() + " " + context.getDebt().getPortfolio();
        if (document) {
            clientFacade.blacklistDocument(clientId, CrmConstants.IDENTITY_DOCUMENT_DNI, comment);
        }

        if (email) {
            clientFacade.blacklistEmail(clientId, comment);
        }

        if (phone) {
            clientFacade.blacklistPhone(clientId, comment);
        }
    }
}
