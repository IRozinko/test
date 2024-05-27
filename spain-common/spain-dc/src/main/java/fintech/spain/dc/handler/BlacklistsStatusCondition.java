package fintech.spain.dc.handler;

import fintech.crm.CrmConstants;
import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import fintech.spain.crm.client.ClientFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BlacklistsStatusCondition implements ConditionHandler {

    private final ClientFacade clientFacade;

    @Autowired
    public BlacklistsStatusCondition(ClientFacade clientFacade) {
        this.clientFacade = clientFacade;
    }

    @Override
    public boolean apply(ConditionContext context) {
        Optional<Boolean> document = context.getParam("document", Boolean.class);
        Optional<Boolean> email = context.getParam("email", Boolean.class);
        Optional<Boolean> phone = context.getParam("phone", Boolean.class);

        Long clientId = context.getDebt().getClientId();
        if (document.isPresent() && document.get() == clientFacade.isDocumentBlacklisted(clientId, CrmConstants.IDENTITY_DOCUMENT_DNI)) {
            return true;
        }

        if (email.isPresent() && email.get() == clientFacade.isEmailBlacklisted(clientId)) {
            return true;
        }

        return phone.isPresent() && phone.get() == clientFacade.isPhoneBlacklisted(clientId);

    }
}
