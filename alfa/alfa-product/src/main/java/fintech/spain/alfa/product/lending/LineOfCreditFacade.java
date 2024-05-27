package fintech.spain.alfa.product.lending;


import fintech.crm.client.model.PrestoDormantsResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface LineOfCreditFacade {

    Long apply(Long clientId, BigDecimal amount, LocalDateTime date);

    PrestoDormantsResponse sendClientToPresto(Long clientId, Long applicationId);

    void completeWorkflow(Long clientId);

    void markClientAsTransferred(Long clientId);
}


