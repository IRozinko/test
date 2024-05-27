package fintech.spain.alfa.product.workflow.common;

import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.platform.web.spi.SpecialLinkService;
import fintech.workflow.spi.WorkflowListener;
import fintech.workflow.spi.WorkflowListenerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class RemoveSpecialLinkActivity implements WorkflowListener {

    private final SpecialLinkType type;

    @Autowired
    private SpecialLinkService service;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RemoveSpecialLinkActivity(SpecialLinkType type) {
        this.type = type;
    }

    @Override
    public void handle(WorkflowListenerContext context) {
        service.deactivateLink(context.getWorkflow().getClientId(), type);
    }
}
