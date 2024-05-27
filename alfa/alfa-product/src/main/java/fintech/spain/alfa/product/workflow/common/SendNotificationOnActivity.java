package fintech.spain.alfa.product.workflow.common;

import com.google.common.collect.ImmutableList;
import fintech.crm.client.Client;
import fintech.crm.client.ClientSegment;
import fintech.crm.client.ClientService;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SendNotificationOnActivity implements ActivityListener {

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    @Autowired
    private CmsContextFactory cmsContextFactory;

    @Autowired
    private ClientService clientService;

    private final String cmsKey;
    private final List<String> excludeClientsInSegments;

    public SendNotificationOnActivity(String cmsKey) {
        this.cmsKey = cmsKey;
        this.excludeClientsInSegments = ImmutableList.of();
    }

    public SendNotificationOnActivity(String cmsKey, List<String> excludeClientsInSegments) {
        this.cmsKey = cmsKey;
        this.excludeClientsInSegments = excludeClientsInSegments;
    }

    @Override
    public void handle(ActivityContext context) {
        Client client = clientService.get(context.getClientId());
        boolean exclude = client.getSegments().stream().map(ClientSegment::getSegment).anyMatch(this.excludeClientsInSegments::contains);
        if (exclude) {
            return;
        }

        Map<String, Object> notificationContext = cmsContextFactory.getContext(context.getWorkflow());
        notificationFactory.fromCustomerService(context.getClientId())
            .loanApplicationId(context.getWorkflow().getApplicationId())
            .loanId(context.getWorkflow().getLoanId())
            .render(cmsKey, notificationContext)
            .send();
    }
}
