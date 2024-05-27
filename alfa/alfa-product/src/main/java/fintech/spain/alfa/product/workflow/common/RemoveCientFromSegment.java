package fintech.spain.alfa.product.workflow.common;

import fintech.crm.client.ClientService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class RemoveCientFromSegment implements ActivityListener {

    @Autowired
    private ClientService clientService;

    private final String segment;

    public RemoveCientFromSegment(String segment) {
        this.segment = segment;
    }

    @Override
    public void handle(ActivityContext context) {
        clientService.removeFromSegment(context.getClientId(), this.segment);
    }
}
