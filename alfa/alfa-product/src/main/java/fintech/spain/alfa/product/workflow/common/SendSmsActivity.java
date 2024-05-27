package fintech.spain.alfa.product.workflow.common;

import fintech.Validate;
import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class SendSmsActivity implements ActivityHandler {

    private final String cmsKey;
    private final Collection<String> contexts;

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    @Autowired
    private AlfaCmsModels cmsModels;

    public SendSmsActivity(String cmsKey, Collection<String> contexts) {
        this.cmsKey = cmsKey;
        this.contexts = contexts;
    }

    @Override
    public ActivityResult handle(ActivityContext context) {
        Long applicationId = context.getWorkflow().getApplicationId();
        Validate.notNull(applicationId, "ApplicationId can not be null");

        log.info("Sending SMS. Type: {}, ApplicationID: {}", cmsKey, applicationId);

        notificationFactory.fromCustomerService(context.getClientId())
            .loanApplicationId(applicationId)
            .render(cmsKey, getCmsContext(applicationId))
            .send();
        return ActivityResult.resolution(Resolutions.OK, "");
    }

    private Map<String, Object> getCmsContext(long applicationId) {
        return contexts.stream()
            .map(contextName -> buildContext(contextName, applicationId))
            .collect(HashMap::new, HashMap::putAll, HashMap::putAll);
    }

    // TODO do not use a special link, use auto login
    private Map<String, Object> buildContext(String contextName, long applicationId) {
        switch (contextName) {
            case AlfaCmsModels.SCOPE_APPLICATION:
                return cmsModels.applicationContext(applicationId);
            case AlfaCmsModels.SCOPE_SPECIAL_LINK:
                return cmsModels.specialLink(applicationId, SpecialLinkType.LOC_SPECIAL_OFFER);
            default:
                return Collections.EMPTY_MAP;
        }
    }
}
