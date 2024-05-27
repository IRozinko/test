package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.crm.client.ClientService;
import fintech.instantor.InstantorService;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.AutoCompletePrecondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class LastInstantorResponseIsValid implements AutoCompletePrecondition {

    @Autowired
    private ClientService clientService;

    @Autowired
    private InstantorService instantorService;

    @Override
    public boolean isTrueFor(ActivityContext context) {
        return responseIsForCurrentDocumentNumber(context);
    }

    private boolean responseIsForCurrentDocumentNumber(ActivityContext context) {
        String documentNumber = clientService.get(context.getClientId()).getDocumentNumber();

        String responseForDocument = context.getAttribute(Attributes.INSTANTOR_RESPONSE_ID)
            .map(id -> instantorService.getResponse(Long.valueOf(id)).getPersonalNumberForVerification())
            .orElse(null);

        return StringUtils.compareIgnoreCase(documentNumber, responseForDocument) == 0;
    }

}
