package fintech.spain.dc.handler;

import fintech.crm.CrmConstants;
import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import fintech.spain.crm.client.ClientFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageBlacklistsAction implements ActionHandler {

    private final ClientFacade clientFacade;

    @Autowired
    public ManageBlacklistsAction(ClientFacade clientFacade) {
        this.clientFacade = clientFacade;
    }

    @Override
    public void handle(ActionContext context) {
        boolean document = context.getParam("document", Boolean.class).orElse(false);
        boolean email = context.getParam("email", Boolean.class).orElse(false);
        boolean phone = context.getParam("phone", Boolean.class).orElse(false);

        Long clientId = context.getDebt().getClientId();
        String comment = context.getAction().getType() + " " + context.getDebt().getPortfolio();
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
