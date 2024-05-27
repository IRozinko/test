package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class InstantorRetryRequested implements ActivityListener {

    @Autowired
    private ClientBankAccountService bankAccountService;

    @Override
    public void handle(ActivityContext context) {
        log.info("Instantor retry requested, removing attached response id and deactivating client's primary bank account");
        context.removeAttribute(Attributes.INSTANTOR_RESPONSE_ID);
        bankAccountService.deactivatePrimaryAccount(context.getClientId());
    }
}
