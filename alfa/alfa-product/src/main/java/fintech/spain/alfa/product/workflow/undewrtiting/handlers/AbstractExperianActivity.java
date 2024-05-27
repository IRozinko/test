package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.Validate;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.spain.experian.ExperianService;
import fintech.spain.experian.model.CaisRequest;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractExperianActivity implements ActivityHandler {

    @Autowired
    protected ExperianService experianService;

    @Autowired
    private ClientService clientService;

    @Override
    public ActivityResult handle(ActivityContext context) {
        Client client = clientService.get(context.getClientId());
        Validate.notBlank(client.getDocumentNumber(), "Client [%s] has no document number", client);

        CaisRequest request = new CaisRequest();
        request.setClientId(context.getClientId());
        request.setDocumentNumber(client.getDocumentNumber());
        return execute(context, request);
    }

    protected abstract ActivityResult execute(ActivityContext context, CaisRequest request);
}
