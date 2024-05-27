package fintech.spain.alfa.product.workflow.common;

import fintech.spain.alfa.product.web.model.PopupType;
import fintech.spain.alfa.product.web.spi.PopupService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class ShowPopupActivity implements ActivityHandler {

    @Autowired
    private PopupService service;

    private final PopupType type;

    public ShowPopupActivity(PopupType type) {
        this.type = type;
    }

    @Override
    public ActivityResult handle(ActivityContext context) {
        service.show(context.getClientId(), type);
        return ActivityResult.resolution(Resolutions.OK, "");
    }
}
